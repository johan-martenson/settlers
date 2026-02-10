package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Carpenter;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Sawmill;
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
public class TestSawmill {

    @Test
    public void testSawmillOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place sawmill
        var point22 = new Point(6, 12);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point22);

        // Deliver two plank and two stone
        Utils.deliverCargo(sawmill0, PLANK);
        Utils.deliverCargo(sawmill0, PLANK);
        Utils.deliverCargo(sawmill0, STONE);
        Utils.deliverCargo(sawmill0, STONE);

        // Assign builder
        Utils.assignBuilder(sawmill0);

        // Verify that this is enough to construct the sawmill
        for (int i = 0; i < 150; i++) {
            assertTrue(sawmill0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(sawmill0.isReady());
    }

    @Test
    public void testSawmillCannotBeConstructedWithTooFewPlanks() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place sawmill
        var point22 = new Point(6, 12);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point22);

        // Deliver one plank and two stone
        Utils.deliverCargo(sawmill0, PLANK);
        Utils.deliverCargo(sawmill0, STONE);
        Utils.deliverCargo(sawmill0, STONE);

        // Assign builder
        Utils.assignBuilder(sawmill0);

        // Verify that this is not enough to construct the sawmill
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(sawmill0.isReady());
    }

    @Test
    public void testSawmillCannotBeConstructedWithTooFewStones() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place sawmill
        var point22 = new Point(6, 12);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point22);

        // Deliver two planks and one stone
        Utils.deliverCargo(sawmill0, PLANK);
        Utils.deliverCargo(sawmill0, PLANK);
        Utils.deliverCargo(sawmill0, STONE);

        // Assign builder
        Utils.assignBuilder(sawmill0);

        // Verify that this is not enough to construct the sawmill
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(sawmill0.isReady());
    }

    @Test
    public void testSawmillNeedsWorker() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Unfinished sawmill doesn't need worker
        assertFalse(sawmill.needsWorker());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());
    }

    @Test
    public void testSawmillCanHoldTotal() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Remove all wood from the headquarters
        Utils.adjustInventoryTo(headquarter, WOOD, 0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Verify that the sawmill can hold the right amount when it's empty
        assertEquals(sawmill.getCanHoldAmount(WOOD), 6);

        // Verify that the sawmill can hold the right amount when production is stopped
        sawmill.stopProduction();

        assertEquals(sawmill.getCanHoldAmount(WOOD), 6);

        // Verify that the sawmill can hold the right amount when it has one piece of wood in its inventory
        sawmill.resumeProduction();

        Utils.deliverCargo(sawmill, WOOD);

        assertEquals(sawmill.getCanHoldAmount(WOOD), 6);
    }

    @Test
    public void testHeadquarterHasAtLeastOneSawmillWorkerAtStart() {
        var headquarter = new Headquarter(null);

        assertTrue(headquarter.getAmount(CARPENTER) >= 1);
    }

    @Test
    public void testSawmillGetsAssignedWorker() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Place a road between the headquarters and the sawmill
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());

        // Verify that a sawmill worker leaves the headquarters
        var sawmillWorker0 = Utils.waitForWorkerOutsideBuilding(Carpenter.class, player0);

        // Let the sawmill worker reach the sawmill
        assertNotNull(sawmillWorker0);
        assertEquals(sawmillWorker0.getTarget(), sawmill.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, sawmillWorker0);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);
    }

    @Test
    public void testSawmillIsNotASoldier() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Place a road between the headquarters and the sawmill
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());

        // Verify that a sawmill worker leaves the headquarters
        var sawmillWorker0 = Utils.waitForWorkerOutsideBuilding(Carpenter.class, player0);

        // Verify that the sawmill worker is not a soldier
        assertNotNull(sawmillWorker0);
        assertFalse(sawmillWorker0.isSoldier());
    }

    @Test
    public void testSawmillWorkerGetsCreatedFromSaw() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Remove all sawmill workers from the headquarters and add a saw
        Utils.adjustInventoryTo(headquarter, CARPENTER, 0);
        Utils.adjustInventoryTo(headquarter, Material.SAW, 1);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Place a road between the headquarters and the sawmill
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());

        // Verify that a sawmill worker leaves the headquarters
        var sawmillWorker0 = Utils.waitForWorkerOutsideBuilding(Carpenter.class, player0);

        // Let the sawmill worker reach the sawmill
        assertNotNull(sawmillWorker0);
        assertEquals(sawmillWorker0.getTarget(), sawmill.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, sawmillWorker0);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);
    }

    @Test
    public void testOccupiedSawmillWithoutWoodProducesNothing() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Occupy the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Verify that the sawmill doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sawmillWorker0.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedSawmillProducesNothing() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Verify that the sawmill doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertFalse(sawmill.isWorking());

            map.stepTime();
        }
    }

    @Test
    public void testOccupiedSawmillWithWoodProducesPlanks() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Occupy the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Deliver wood to the sawmill
        Utils.deliverCargo(sawmill, WOOD);
        Utils.deliverCargo(sawmill, WOOD);

        // Verify that the sawmill produces planks
        assertFalse(sawmill.isWorking());

        for (int i = 0; i < 99; i++) {
            map.stepTime();

            assertFalse(sawmill.isWorking());
            assertFalse(sawmillWorker0.isWorking());
        }

        for (int i = 0; i < 50; i++) {
            map.stepTime();

            assertTrue(sawmill.isWorking());
            assertTrue(sawmillWorker0.isWorking());
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sawmillWorker0.getCargo());
        }

        map.stepTime();

        assertNotNull(sawmillWorker0.getCargo());
        assertEquals(sawmillWorker0.getCargo().getMaterial(), PLANK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
        assertFalse(sawmill.isWorking());
    }

    @Test
    public void testSawmillWorkerLeavesPlanksAtTheFlag() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Place a road between the headquarters and the sawmill
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Occupy the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Deliver wood to the sawmill
        Utils.deliverCargo(sawmill, WOOD);
        Utils.deliverCargo(sawmill, WOOD);

        // Verify that the sawmill produces planks
        for (int i = 0; i < 149; i++) {
            map.stepTime();

            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sawmillWorker0.getCargo());
        }

        map.stepTime();

        assertNotNull(sawmillWorker0.getCargo());
        assertEquals(sawmillWorker0.getCargo().getMaterial(), PLANK);
        assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());

        // Verify that the sawmill worker leaves the cargo at the flag
        assertEquals(sawmillWorker0.getTarget(), sawmill.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker0, sawmill.getFlag().getPosition());

        assertFalse(sawmill.getFlag().getStackedCargo().isEmpty());
        assertNull(sawmillWorker0.getCargo());
        assertEquals(sawmillWorker0.getTarget(), sawmill.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, sawmillWorker0);

        assertTrue(sawmillWorker0.isInsideBuilding());
    }

    @Test
    public void testPlankCargoIsDeliveredToGuardHouseUnderConstructionWhichIsCloserThanHeadquarters() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Place flag
        var point2 = new Point(11, 3);
        var flag = map.placeFlag(player0, point2);

        // Place road between the headquarters and the flag
        var road1 = map.placeAutoSelectedRoad(player0, flag, headquarter.getFlag());

        // Place the sawmill
        var point1 = new Point(14, 4);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Connect the sawmill with the flag
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), flag);

        // Wait for the sawmill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(sawmill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        // Remove all planks from the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 0);

        // Place guard house
        var point4 = new Point(10, 4);
        var guardHouse = map.placeBuilding(new GuardHouse(player0), point4);

        // Deliver wood to the sawmill
        Utils.deliverCargo(sawmill, WOOD);

        // Wait for the courier on the road between the guard house and the quarry hut to have a cargo
        Utils.waitForFlagToGetStackedCargo(map, sawmill.getFlag(), 1);

        assertEquals(sawmill.getFlag().getStackedCargo().getFirst().getMaterial(), PLANK);

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that the courier delivers the cargo to the guard house (and not the headquarters)
        assertEquals(sawmill.getAmount(PLANK), 0);
        assertTrue(guardHouse.needsMaterial(PLANK));
        assertTrue(guardHouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), guardHouse.getPosition());

        assertEquals(guardHouse.getAmount(PLANK), 1);
    }

    @Test
    public void testPlankIsNotDeliveredToStorehouseUnderConstructionThatDoesntNeedPlanks() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Adjust the inventory so that there are no stones, planks, or wood
        Utils.adjustInventoryTo(headquarter, PLANK, 0);
        Utils.adjustInventoryTo(headquarter, STONE, 0);
        Utils.adjustInventoryTo(headquarter, WOOD, 0);

        // Place storehouse
        var point4 = new Point(10, 4);
        var storehouse = map.placeBuilding(new Storehouse(player0), point4);

        // Connect the storehouse to the headquarters
        var road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Add planks to the storehouse doesn't need any more planks
        Utils.deliverCargos(storehouse, PLANK, 4);

        assertFalse(storehouse.needsMaterial(PLANK));

        // Place the sawmill
        var point1 = new Point(14, 4);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Connect the sawmill with the storehouse
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), storehouse.getFlag());

        // Deliver the needed material to construct the sawmill
        Utils.deliverCargos(sawmill, PLANK, 2);
        Utils.deliverCargos(sawmill, STONE, 2);

        // Wait for the sawmill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(sawmill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        // Wait for the courier on the road between the storehouse and the sawmill to have a plank cargo
        Utils.deliverCargo(sawmill, WOOD);

        Utils.waitForFlagToGetStackedCargo(map, sawmill.getFlag(), 1);

        assertEquals(sawmill.getFlag().getStackedCargo().getFirst().getMaterial(), PLANK);

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters
        assertEquals(headquarter.getAmount(PLANK), 0);
        assertEquals(sawmill.getAmount(PLANK), 0);
        assertFalse(storehouse.needsMaterial(PLANK));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(PLANK));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testPlankIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Adjust the inventory so that there are no planks, stones or wood*/
        Utils.adjustInventoryTo(headquarter, PLANK, 0);
        Utils.adjustInventoryTo(headquarter, WOOD, 0);
        Utils.adjustInventoryTo(headquarter, STONE, 0);

        // Place storehouse
        var point4 = new Point(10, 4);
        var storehouse = map.placeBuilding(new Storehouse(player0), point4);

        // Deliver planks to the storehouse, so it only needs one more plank
        Utils.deliverCargos(storehouse, PLANK, 3);

        assertTrue(storehouse.needsMaterial(PLANK));
        assertEquals(storehouse.getAmount(PLANK), 3);

        // Connect the storehouse to the headquarters
        var road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Place the sawmill
        var point1 = new Point(14, 4);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Connect the sawmill with the storehouse
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), storehouse.getFlag());

        // Deliver the needed material to construct the sawmill
        Utils.deliverCargos(sawmill, PLANK, 2);
        Utils.deliverCargos(sawmill, STONE, 2);

        // Wait for the sawmill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(sawmill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        // Wait for the flag on the road between the storehouse and the sawmill to have a plank cargo
        Utils.deliverCargo(sawmill, WOOD);

        Utils.waitForFlagToGetStackedCargo(map, sawmill.getFlag(), 1);

        assertEquals(sawmill.getFlag().getStackedCargo().getFirst().getMaterial(), PLANK);

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier(), PLANK);

        assertEquals(road0.getCourier().getCargo().getMaterial(), PLANK);
        assertEquals(road0.getCourier().getCargo().getTarget(), storehouse);

        // Verify that no plank is delivered from the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 1);

        assertEquals(storehouse.getCanHoldAmount(PLANK) - storehouse.getAmount(PLANK), 1);
        assertFalse(storehouse.needsMaterial(PLANK));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(PLANK), 1);
    }

    @Test
    public void testProductionOfOnePlankConsumesOneWood() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Occupy the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        // Deliver wood to the sawmill
        Utils.deliverCargo(sawmill, WOOD);

        // Wait until the sawmill worker produces a plank
        assertEquals(sawmill.getAmount(WOOD), 1);

        Utils.fastForward(150, map);

        assertEquals(sawmill.getAmount(WOOD), 0);
        assertTrue(sawmill.needsMaterial(WOOD));
    }

    @Test
    public void testProductionCountdownStartsWhenWoodIsAvailable() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Occupy the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        // Fast forward so that the sawmill worker would produced planks if it had had any wood
        Utils.fastForward(150, map);

        assertNull(sawmillWorker0.getCargo());

        // Deliver wood to the sawmill
        Utils.deliverCargo(sawmill, WOOD);

        // Verify that it takes 50 steps for the sawmill worker to produce the plank
        for (int i = 0; i < 50; i++) {
            assertNull(sawmillWorker0.getCargo());

            map.stepTime();
        }

        assertNotNull(sawmillWorker0.getCargo());
    }

    @Test
    public void testSawmillWithoutConnectedStorageKeepsProducing() throws Exception {

        // Creating new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(8, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Occupy the sawmill
        Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Deliver material to the sawmill
        Utils.deliverCargo(sawmill0, WOOD);
        Utils.deliverCargo(sawmill0, WOOD);

        // Let the sawmill worker rest
        Utils.fastForward(100, map);

        // Wait for the sawmill worker to produce a new plank cargo
        Utils.fastForward(50, map);

        var sawmillWorker = sawmill0.getWorker();

        assertNotNull(sawmillWorker.getCargo());

        // Verify that the sawmill worker puts the plank cargo at the flag
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());
        assertTrue(sawmill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        assertNull(sawmillWorker.getCargo());
        assertFalse(sawmill0.getFlag().getStackedCargo().isEmpty());

        // Wait for the worker to go back to the sawmill
        assertEquals(sawmillWorker.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getPosition());

        // Wait for the worker to rest and produce another cargo
        Utils.fastForward(150, map);

        assertNotNull(sawmillWorker.getCargo());

        // Verify that the second cargo is put at the flag
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        assertNull(sawmillWorker.getCargo());
        assertEquals(sawmill0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(8, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Deliver material to the sawmill
        Utils.deliverCargo(sawmill0, WOOD);
        Utils.deliverCargo(sawmill0, WOOD);

        // Occupy the sawmill
        Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Let the sawmill worker rest
        Utils.fastForward(100, map);

        // Wait for the sawmill worker to produce a new plank cargo
        Utils.fastForward(50, map);

        var sawmillWorker = sawmill0.getWorker();

        assertNotNull(sawmillWorker.getCargo());

        // Verify that the sawmill worker puts the plank cargo at the flag
        assertEquals(sawmillWorker.getTarget(), sawmill0.getFlag().getPosition());
        assertTrue(sawmill0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, sawmill0.getFlag().getPosition());

        assertNull(sawmillWorker.getCargo());
        assertFalse(sawmill0.getFlag().getStackedCargo().isEmpty());

        // Wait to let the cargo remain at the flag without any connection to the storage
        var cargo =sawmill0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), sawmill0.getFlag().getPosition());

        // Connect the sawmill with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill0.getFlag());

        // Assign a courier to the road
        var courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        // Wait for the courier to reach the idle point of the road
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), sawmill0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        // Verify that the courier walks to pick up the cargo
        for (int i = 0; i < 1000; i++) {
            if (courier.getTarget().equals(sawmill0.getFlag().getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(courier.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        // Verify that the courier has picked up the cargo
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        // Verify that the courier delivers the cargo to the headquarters
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(PLANK);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        // Verify that the courier has delivered the cargo to the headquarters
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(PLANK), amount + 1);
    }

    @Test
    public void testSawmillWorkerGoesBackToStorageWhenSawmillIsDestroyed() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(8, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Occupy the sawmill
        Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Destroy the sawmill
        var sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(CARPENTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());

        // Verify that the sawmill worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(CARPENTER), amount + 1);
    }

    @Test
    public void testSawmillWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenSawmillIsDestroyed() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(8, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Connect the sawmill with the headquarters
        map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Occupy the sawmill
        Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Destroy the sawmill
        var sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        // Verify that the worker plans to use the roads
        boolean firstStep = true;
        for (var point : sawmillWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testDestroyedSawmillIsRemovedAfterSomeTime() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(8, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Connect the sawmill with the headquarters
        map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Destroy the sawmill
        sawmill0.tearDown();

        assertTrue(sawmill0.isBurningDown());

        // Wait for the sawmill to stop burning
        Utils.fastForward(50, map);

        assertTrue(sawmill0.isDestroyed());

        // Wait for the sawmill to disappear
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), sawmill0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(sawmill0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(8, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Remove the flag and verify that the driveway is removed
        assertNotNull(map.getRoad(sawmill0.getPosition(), sawmill0.getFlag().getPosition()));

        map.removeFlag(sawmill0.getFlag());

        assertNull(map.getRoad(sawmill0.getPosition(), sawmill0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(8, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Tear down the building and verify that the driveway is removed
        assertNotNull(map.getRoad(sawmill0.getPosition(), sawmill0.getFlag().getPosition()));

        sawmill0.tearDown();

        assertNull(map.getRoad(sawmill0.getPosition(), sawmill0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInSawmillCanBeStopped() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(10, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Connect the sawmill and the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter.getFlag());

        // Finish the sawmill
        Utils.constructHouse(sawmill0);

        // Assign a worker to the sawmill
        var carpenter = new Carpenter(player0, map);

        Utils.occupyBuilding(carpenter, sawmill0);

        assertTrue(carpenter.isInsideBuilding());

        // Deliver wood to the sawmill
        Utils.deliverCargo(sawmill0, WOOD);

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the sawmill worker to produce cargo
        Utils.fastForwardUntilWorkerProducesCargo(map, carpenter);

        assertEquals(carpenter.getCargo().getMaterial(), PLANK);

        // Wait for the worker to deliver the cargo
        assertEquals(carpenter.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, sawmill0.getFlag().getPosition());

        // Stop production and verify that no plank is produced
        sawmill0.stopProduction();

        assertFalse(sawmill0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(carpenter.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInSawmillCanBeResumed() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(10, 8);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Connect the sawmill and the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter.getFlag());

        // Finish the sawmill
        Utils.constructHouse(sawmill0);

        // Assign a worker to the sawmill
        var carpenter = new Carpenter(player0, map);

        Utils.occupyBuilding(carpenter, sawmill0);

        assertTrue(carpenter.isInsideBuilding());

        // Deliver wood to the sawmill
        Utils.deliverCargo(sawmill0, WOOD);

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the sawmill worker to produce plank
        Utils.fastForwardUntilWorkerProducesCargo(map, carpenter);

        assertEquals(carpenter.getCargo().getMaterial(), PLANK);

        // Wait for the worker to deliver the cargo
        assertEquals(carpenter.getTarget(), sawmill0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, sawmill0.getFlag().getPosition());

        // Stop production
        sawmill0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(carpenter.getCargo());

            map.stepTime();
        }

        // Resume production and verify that the sawmill produces plank again
        sawmill0.resumeProduction();

        assertTrue(sawmill0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, carpenter);

        assertNotNull(carpenter.getCargo());
    }

    @Test
    public void testAssignedSawmillWorkerHasCorrectlySetPlayer() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 50);

        // Place headquarters
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(20, 14);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Connect the sawmill with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill0.getFlag());

        // Wait for sawmill worker to get assigned and leave the headquarters
        var workers = Utils.waitForWorkersOutsideBuilding(Carpenter.class, 1, player0);

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
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place player 2's headquarters
        var point10 = new Point(70, 70);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        // Place player 0's headquarters
        var point0 = new Point(11, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place var for player 0
        var point2 = new Point(21, 9);
        var fortress0 = map.placeBuilding(new Fortress(player0), point2);

        // Finish construction of the fortress
        Utils.constructHouse(fortress0);

        // Occupy the fortress
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        // Place sawmill close to the new border
        var point4 = new Point(28, 18);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point4);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Occupy the sawmill
        var worker = Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Verify that the worker goes back to its own storage when the fortress is torn down
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testSawmillWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place sawmill
        var point2 = new Point(14, 4);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill0.getFlag());

        // Wait for the sawmill worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Carpenter.class, 1, player0);

        Carpenter carpenter = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Carpenter) {
                carpenter = (Carpenter) worker;
            }
        }

        assertNotNull(carpenter);
        assertEquals(carpenter.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the sawmill worker has started walking
        assertFalse(carpenter.isExactlyAtPoint());

        // Remove the next road
        map.removeRoad(road1);

        // Verify that the sawmill worker continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, flag0.getPosition());

        assertEquals(carpenter.getPosition(), flag0.getPosition());

        // Verify that the sawmill worker returns to the headquarters when it reaches the flag
        assertEquals(carpenter.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, headquarter0.getPosition());
    }

    @Test
    public void testSawmillWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place sawmill
        var point2 = new Point(14, 4);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill0.getFlag());

        // Wait for the sawmill worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Carpenter.class, 1, player0);

        Carpenter carpenter = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Carpenter) {
                carpenter = (Carpenter) worker;
            }
        }

        assertNotNull(carpenter);
        assertEquals(carpenter.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the sawmill worker has started walking
        assertFalse(carpenter.isExactlyAtPoint());

        // Remove the current road
        map.removeRoad(road0);

        // Verify that the sawmill worker continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, flag0.getPosition());

        assertEquals(carpenter.getPosition(), flag0.getPosition());

        // Verify that the sawmill worker continues to the final flag
        assertEquals(carpenter.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, sawmill0.getFlag().getPosition());

        // Verify that the sawmill worker goes out to sawmill instead of going directly back
        assertNotEquals(carpenter.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testSawmillWorkerReturnsToStorageIfSawmillIsDestroyed() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place sawmill
        var point2 = new Point(14, 4);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, sawmill0.getFlag());

        // Wait for the sawmill worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Carpenter.class, 1, player0);

        Carpenter carpenter = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Carpenter) {
                carpenter = (Carpenter) worker;
            }
        }

        assertNotNull(carpenter);
        assertEquals(carpenter.getTarget(), sawmill0.getPosition());

        // Wait for the sawmill worker to reach the first flag
        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, flag0.getPosition());

        map.stepTime();

        // See that the sawmill worker has started walking
        assertFalse(carpenter.isExactlyAtPoint());

        // Tear down the sawmill
        sawmill0.tearDown();

        // Verify that the sawmill worker continues walking to the next flag
        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, sawmill0.getFlag().getPosition());

        assertEquals(carpenter.getPosition(), sawmill0.getFlag().getPosition());

        // Verify that the sawmill worker goes back to storage
        assertEquals(carpenter.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testSawmillWorkerGoesOffroadBackToClosestStorageWhenSawmillIsDestroyed() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(17, 17);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Occupy the sawmill
        Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Place a second storage closer to the sawmill
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the sawmill
        var sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(CARPENTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, storehouse0.getPosition());

        // Verify that the sawmill worker is stored correctly in the headquarters
        assertEquals(storehouse0.getAmount(CARPENTER), amount + 1);
    }

    @Test
    public void testSawmillWorkerReturnsOffroadAndAvoidsBurningStorageWhenSawmillIsDestroyed() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 17);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(17, 17);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Occupy the sawmill
        Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Place a second storage closer to the sawmill
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Destroy the sawmill
        var sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(CARPENTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());

        // Verify that the sawmill worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(CARPENTER), amount + 1);
    }

    @Test
    public void testSawmillWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenSawmillIsDestroyed() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(17, 17);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Occupy the sawmill
        Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Place a second storage closer to the sawmill
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Wait for the storage to burn down
        Utils.waitForBuildingToBurnDown(storehouse0);

        // Destroy the sawmill
        var sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(CARPENTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());

        // Verify that the sawmill worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(CARPENTER), amount + 1);
    }

    @Test
    public void testSawmillWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenSawmillIsDestroyed() throws Exception {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(17, 17);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Occupy the sawmill
        Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Place a second storage closer to the sawmill
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the sawmill
        var sawmillWorker = sawmill0.getWorker();

        assertTrue(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getPosition(), sawmill0.getPosition());

        sawmill0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(sawmillWorker.isInsideBuilding());
        assertEquals(sawmillWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(CARPENTER);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker, headquarter0.getPosition());

        // Verify that the sawmill worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(CARPENTER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place sawmill
        var point26 = new Point(17, 17);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point26);

        // Place road to connect the headquarters and the sawmill
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill0.getFlag());

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Wait for a worker to start walking to the building
        var worker = Utils.waitForWorkersOutsideBuilding(Carpenter.class, 1, player0).getFirst();

        // Wait for the worker to get to the building's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, sawmill0.getFlag().getPosition());

        // Tear down the building
        sawmill0.tearDown();

        // Verify that the worker goes to the building and then returns to the headquarters instead of entering
        assertEquals(worker.getTarget(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, sawmill0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testSawmillWithoutResourcesHasZeroProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Populate the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Verify that the productivity is 0% when the sawmill doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(sawmill.getFlag().getStackedCargo().isEmpty());
            assertNull(sawmillWorker0.getCargo());
            assertEquals(sawmill.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testSawmillWithAbundantResourcesHasFullProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Populate the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Connect the sawmill with the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill.getFlag());

        // Make the sawmill create some bread with full resources available
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (sawmill.needsMaterial(WOOD)) {
                Utils.deliverCargo(sawmill, WOOD);
            }
        }

        // Verify that the productivity is 100% and stays there
        assertEquals(sawmill.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (sawmill.needsMaterial(WOOD)) {
                Utils.deliverCargo(sawmill, WOOD);
            }

            assertEquals(sawmill.getProductivity(), 100);
        }
    }

    @Test
    public void testSawmillLosesProductivityWhenResourcesRunOut() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Populate the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Connect the sawmill with the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), sawmill.getFlag());

        // Make the sawmill create some planks with full resources available
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (sawmill.needsMaterial(WOOD) && sawmill.getAmount(WOOD) < 2) {
                Utils.deliverCargo(sawmill, WOOD);
            }
        }

        // Verify that the productivity goes down when resources run out
        assertEquals(sawmill.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(sawmill.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedSawmillHasNoProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Verify that the unoccupied sawmill is unproductive
        for (int i = 0; i < 1000; i++) {
            assertEquals(sawmill.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testSawmillCanProduce() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(10, 10);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill0);

        // Populate the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill0);

        // Verify that the sawmill can produce
        assertTrue(sawmill0.canProduce());
    }

    @Test
    public void testSawmillReportsCorrectOutput() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(6, 12);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Construct the sawmill
        Utils.constructHouse(sawmill0);

        // Verify that the reported output is correct
        assertEquals(sawmill0.getProducedMaterial().length, 1);
        assertEquals(sawmill0.getProducedMaterial()[0], PLANK);
    }

    @Test
    public void testSawmillReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(6, 12);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(sawmill0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(sawmill0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(sawmill0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(sawmill0.getCanHoldAmount(PLANK), 2);
        assertEquals(sawmill0.getCanHoldAmount(STONE), 2);

        for (var material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(sawmill0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testSawmillReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(6, 12);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Construct the sawmill
        Utils.constructHouse(sawmill0);

        // Verify that the reported needed construction material is correct
        assertEquals(sawmill0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(sawmill0.getTypesOfMaterialNeeded().contains(WOOD));
        assertEquals(sawmill0.getCanHoldAmount(WOOD), 6);

        for (var material : Material.values()) {
            if (material == WOOD) {
                continue;
            }

            assertEquals(sawmill0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testSawmillWaitsWhenFlagIsFull() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(16, 6);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Connect the sawmill with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Wait for the sawmill to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(sawmill);
        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        // Give material to the sawmill
        Utils.deliverCargo(sawmill, WOOD);
        Utils.deliverCargo(sawmill, WOOD);
        Utils.deliverCargo(sawmill, WOOD);

        // Fill the flag with flour cargos
        Utils.placeCargos(map, FLOUR, 8, sawmill.getFlag(), headquarter);

        // Remove the road
        map.removeRoad(road0);

        // Verify that the sawmill waits for the flag to get empty and produces nothing
        for (int i = 0; i < 300; i++) {
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);
            assertNull(sawmill.getWorker().getCargo());

            map.stepTime();
        }

        // Reconnect the sawmill with the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Wait for the courier to pick up one of the cargos
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(sawmill.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(sawmill.getFlag().getStackedCargo().size(), 7);

        // Verify that the worker produces a cargo of flour and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmill.getWorker(), PLANK);
    }

    @Test
    public void testSawmillDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place sawmill
        var point1 = new Point(16, 6);
        var sawmill = map.placeBuilding(new Sawmill(player0), point1);

        // Connect the sawmill with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Wait for the sawmill to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(sawmill);
        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        // Give material to the sawmill
        Utils.deliverCargo(sawmill, WOOD);
        Utils.deliverCargo(sawmill, WOOD);
        Utils.deliverCargo(sawmill, WOOD);

        // Fill the flag with cargos
        Utils.placeCargos(map, FLOUR, 8, sawmill.getFlag(), headquarter);

        // Remove the road
        map.removeRoad(road0);

        // The sawmill waits for the flag to get empty and produces nothing
        for (int i = 0; i < 300; i++) {
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);
            assertNull(sawmill.getWorker().getCargo());

            map.stepTime();
        }

        // Reconnect the sawmill with the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Wait for the courier to pick up one of the cargos
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(sawmill.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(sawmill.getFlag().getStackedCargo().size(), 7);

        // Remove the road
        map.removeRoad(road1);

        // The worker produces a cargo and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmill.getWorker(), PLANK);

        // Wait for the worker to put the cargo on the flag
        assertEquals(sawmill.getWorker().getTarget(), sawmill.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmill.getWorker(), sawmill.getFlag().getPosition());

        assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);

        // Verify that the sawmill doesn't produce anything because the flag is full
        for (int i = 0; i < 400; i++) {
            assertEquals(sawmill.getFlag().getStackedCargo().size(), 8);
            assertNull(sawmill.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenWoodDeliveryAreBlockedSawmillFillsUpFlagAndThenStops() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place Sawmill
        var point1 = new Point(7, 9);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Place road to connect the sawmill with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        // Wait for the sawmill to get constructed and occupied
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(sawmill0);

        var sawmillWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill0);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill0);
        assertEquals(sawmill0.getWorker(), sawmillWorker0);

        // Add a lot of material to the headquarters for the sawmill to consume
        Utils.adjustInventoryTo(headquarter0, WOOD, 40);

        // Block storage of planks
        headquarter0.blockDeliveryOfMaterial(PLANK);

        // Verify that the sawmill puts eight planks on the flag and then stops
        Utils.waitForFlagToGetStackedCargo(map, sawmill0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker0, sawmill0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(sawmill0.getFlag().getStackedCargo().size(), 8);
            assertTrue(sawmillWorker0.isInsideBuilding());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), PLANK);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndSawmillIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place sawmill
        var point2 = new Point(18, 6);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point2);

        // Place road to connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the sawmill
        var road1 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the sawmill and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, sawmill0);

        // Add a lot of material to the headquarters for the sawmill to consume
        Utils.adjustInventoryTo(headquarter0, WOOD, 40);

        // Wait for the sawmill and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, sawmill0);

        var sawmillWorker0 = sawmill0.getWorker();

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill0);
        assertEquals(sawmill0.getWorker(), sawmillWorker0);

        // Verify that the worker goes to the storage when the sawmill is torn down
        headquarter0.blockDeliveryOfMaterial(CARPENTER);

        sawmill0.tearDown();

        map.stepTime();

        assertFalse(sawmillWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker0, sawmill0.getFlag().getPosition());

        assertEquals(sawmillWorker0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, sawmillWorker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(sawmillWorker0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndSawmillIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place sawmill
        var point2 = new Point(18, 6);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point2);

        // Place road to connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the sawmill
        var road1 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the sawmill and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, sawmill0);

        // Add a lot of material to the headquarters for the sawmill to consume
        Utils.adjustInventoryTo(headquarter0, WOOD, 40);

        // Wait for the sawmill and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, sawmill0);

        var sawmillWorker0 = sawmill0.getWorker();

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill0);
        assertEquals(sawmill0.getWorker(), sawmillWorker0);

        // Verify that the worker goes to the storage off-road when the sawmill is torn down
        headquarter0.blockDeliveryOfMaterial(CARPENTER);

        sawmill0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(sawmillWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker0, sawmill0.getFlag().getPosition());

        assertEquals(sawmillWorker0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, sawmillWorker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(sawmillWorker0));
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
        Utils.adjustInventoryTo(headquarter0, CARPENTER, 1);

        assertEquals(headquarter0.getAmount(CARPENTER), 1);

        headquarter0.pushOutAll(CARPENTER);

        for (int i = 0; i < 10; i++) {
            var worker = Utils.waitForWorkerOutsideBuilding(Carpenter.class, player0);

            assertEquals(headquarter0.getAmount(CARPENTER), 0);
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
        Utils.adjustInventoryTo(headquarter0, CARPENTER, 1);

        headquarter0.blockDeliveryOfMaterial(CARPENTER);
        headquarter0.pushOutAll(CARPENTER);

        var worker = Utils.waitForWorkerOutsideBuilding(Carpenter.class, player0);

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

        // Place sawmill
        var point1 = new Point(7, 9);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Place road to connect the sawmill with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the sawmill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(sawmill0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarters */
          
       
        headquarter0.blockDeliveryOfMaterial(CARPENTER);

        Worker worker = sawmill0.getWorker();

        sawmill0.tearDown();

        assertEquals(worker.getPosition(), sawmill0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, sawmill0.getFlag().getPosition());

        assertEquals(worker.getPosition(), sawmill0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), sawmill0.getPosition());
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

        // Place sawmill
        var point1 = new Point(7, 9);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        // Place road to connect the sawmill with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the sawmill to get constructed
        Utils.waitForBuildingToBeConstructed(sawmill0);

        // Wait for a sawmill worker to start walking to the sawmill
        Carpenter carpenter = Utils.waitForWorkerOutsideBuilding(Carpenter.class, player0);

        // Wait for the sawmill worker to go past the headquarters's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, headquarter0.getFlag().getPosition());

        map.stepTime();

        // Verify that the sawmill worker goes away and dies when the house has been torn down and storage is not possible
        assertEquals(carpenter.getTarget(), sawmill0.getPosition());

        headquarter0.blockDeliveryOfMaterial(CARPENTER);

        sawmill0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, sawmill0.getFlag().getPosition());

        assertEquals(carpenter.getPosition(), sawmill0.getFlag().getPosition());
        assertNotEquals(carpenter.getTarget(), headquarter0.getPosition());
        assertFalse(carpenter.isInsideBuilding());
        assertNull(sawmill0.getWorker());
        assertNotNull(carpenter.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, carpenter, carpenter.getTarget());

        var point = carpenter.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(carpenter.isDead());
            assertEquals(carpenter.getPosition(), point);
            assertTrue(map.getWorkers().contains(carpenter));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(carpenter));
    }
}
