package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.TransportCategory;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.StorehouseWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Rank.PRIVATE_RANK;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestStorehouse {

    // Verified in real game: builder finishes construction, enters the newly built storehouse, becomes occupied with
    // one builder in inventory

    /*
    TODO:
      - material can be pushed out:
          - push cargo from headquarters to storehouse DONE
          - push worker from headquarters to storehouse DONE
          - push cargo from headquarters without any place to store - headquarters flag fills up DONE
          - push out follows priority order DONE
      - material can be blocked:
          - deliveries go to another storehouse DONE
          - test for each type of house/worker: (DONE)
            - flags fill up and then deliveries stop if there is nowhere to put them
            - push worker from headquarters without any place to store - worker goes away and dies
            - push worker from headquarters without blocking - worker goes out and in again
            - when house is burned and storing of worker is blocked, worker goes to other storehouse
            - when house is burned, storing of worker is blocked, and there is no other place to store - worker walks away and dies
      - push out and block at the same time - material and worker
     */

    @Test
    public void testStorehouseOnlyNeedsFourPlanksAndThreeStonesForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place storehouse
        var point22 = new Point(6, 12);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point22);

        // Deliver four plank and three stone
        var plankCargo = new Cargo(PLANK, map);
        var stoneCargo = new Cargo(STONE, map);

        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(storehouse0);

        // Verify that this is enough to construct the storehouse
        for (int i = 0; i < 150; i++) {
            assertTrue(storehouse0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(storehouse0.isReady());
    }

    @Test
    public void testStorehouseCannotBeConstructedWithTooFewPlanks() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place storehouse
        var point22 = new Point(6, 12);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point22);

        // Deliver three planks and three stone
        var plankCargo = new Cargo(PLANK, map);
        var stoneCargo = new Cargo(STONE, map);

        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(storehouse0);

        // Verify that this is not enough to construct the storehouse
        for (int i = 0; i < 500; i++) {
            assertTrue(storehouse0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(storehouse0.isReady());
    }

    @Test
    public void testStorehouseCannotBeConstructedWithTooFewStones() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place storehouse
        var point22 = new Point(6, 12);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point22);

        // Deliver four planks and two stones
        var plankCargo = new Cargo(PLANK, map);
        var stoneCargo = new Cargo(STONE, map);

        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(storehouse0);

        // Verify that this is not enough to construct the storehouse
        for (int i = 0; i < 500; i++) {
            assertTrue(storehouse0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(storehouse0.isReady());
    }

    @Test
    public void testStorehouseIsConstructedWithRequiredResources() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place storehouse
        var point22 = new Point(6, 12);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point22);

        // Deliver four planks and two stones
        var plankCargo = new Cargo(PLANK, map);
        var stoneCargo = new Cargo(STONE, map);

        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(plankCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);
        storehouse0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(storehouse0);

        // Verify that this is not enough to construct the storehouse
        for (int i = 0; i < 1000; i++) {

            if (storehouse0.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(storehouse0.isReady());
    }

    @Test
    public void testUnfinishedStorehouseNotNeedsWorker() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point3 = new Point(7, 9);
        var storage = map.placeBuilding(new Storehouse(player0), point3);

        // Verify that an unfinished storehouse doesn't need a worker
        assertFalse(storage.needsWorker());
    }

    @Test
    public void testStorehouseDoesNotNeedWorker() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point3 = new Point(7, 9);
        var storage = map.placeBuilding(new Storehouse(player0), point3);

        // Finish construction of the storehouse
        Utils.constructHouse(storage);

        assertTrue(storage.isReady());

        // Verify that the finished storehouse doesn't need a worker
        assertFalse(storage.needsWorker());
    }

    @Test
    public void testBuilderBecomesStorehouseWorkerAfterFinishingStorehouse() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point3 = new Point(7, 9);
        var storehouse = map.placeBuilding(new Storehouse(player0), point3);

        // Connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Wait for a builder to start working on the storehouse
        var builder = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertEquals(storehouse.getPosition(), builder.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(builder, storehouse.getPosition());

        // Verify that when the storehouse is fully built, the builder enters it and occupies it.
        // -- Also verify that no storehouse worker goes out from the headquarters!
        Utils.fastForwardUntil(map, () -> !storehouse.isPlanned() &&
                !storehouse.isUnderConstruction() &&
                !builder.isHammering()
        );

        assertTrue(storehouse.isUnoccupied());
        assertFalse(storehouse.isUnderConstruction());

        // Give the builder a little extra time to notice that the construction is done
        Utils.fastForwardUntil(200, map, () -> Objects.equals(builder.getTarget(), storehouse.getPosition()));

        assertTrue(
                Objects.equals(storehouse.getPosition(), builder.getTarget()) ||
                Objects.equals(storehouse.getPosition(), builder.getPosition())
                );

        assertTrue(storehouse.isUnoccupied());
        assertEquals(storehouse.getPosition(), builder.getTarget());
        assertEquals(0, storehouse.getAmount(HAMMER));

        Utils.fastForwardUntilWorkerReachesPoint(
                builder,
                storehouse.getPosition(),
                () -> assertTrue(map.getWorkers().stream().noneMatch(worker -> worker instanceof StorehouseWorker && !worker.isInsideBuilding()))
        );

        assertTrue(storehouse.isOccupied());
        assertFalse(map.getWorkers().contains(builder));
        assertEquals(1, storehouse.getAmount(HAMMER));
        assertFalse(map.getWorkers().stream().anyMatch(worker -> worker instanceof StorehouseWorker && !worker.isInsideBuilding()));
    }

    @Test
    public void testStorehouseWorkerIsNotASoldier() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse and wait for it to get constructed and occupied
        var point3 = new Point(7, 9);
        var storehouse = map.placeBuilding(new Storehouse(player0), point3);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        var storehouseWorker = Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Verify that the storehouse worker isn't a soldier
        assertFalse(storehouseWorker.isSoldier());
        assertFalse(storehouseWorker instanceof Soldier);
    }

    @Test
    public void testStorehouseWorkerRests() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse, connect it, and wait for it to get constructed and occupied
        var point3 = new Point(7, 9);
        var storehouse = map.placeBuilding(new Storehouse(player0), point3);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        var storehouseWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Verify that the storehouse worker rests
        for (int i = 0; i < 50; i++) {
            assertTrue(storehouseWorker0.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testStorehouseWorkerRestsThenDeliversCargo() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(11, 9);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        // Place storehouse
        var point3 = new Point(7, 9);
        var storehouse = map.placeBuilding(new Storehouse(player0), point3.upLeft());

        // Connect the storehouse with the headquarters and wait for it to get constructed and occupied
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        var storehouseWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Disconnect the storehouse from the headquarters
        map.removeRoad(road0);

        // Add planks to the storehouse
        Utils.adjustInventoryTo(storehouse, PLANK, 30);

        // Connect the storehouse with the woodcutter
        var point2 = new Point(9, 9);
        map.placeRoad(player0, point1, point2, point3);

        // Verify that the storehouse worker rests and the comes out holding a plank
        Utils.fastForward(20, map, () -> assertTrue(storehouseWorker0.isInsideBuilding()));

        assertFalse(storehouseWorker0.isInsideBuilding());
        assertNotNull(storehouseWorker0.getCargo());
        assertEquals(storehouseWorker0.getTarget(), storehouse.getFlag().getPosition());
        assertTrue(storehouse.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(storehouseWorker0, storehouse.getFlag().getPosition());

        assertNull(storehouseWorker0.getCargo());
        assertFalse(storehouse.getFlag().getStackedCargo().isEmpty());
    }

    @Test
    public void testStorehouseWorkerGoesBackToStorehouseAfterDelivery() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(11, 9);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        // Place storehouse and wait for it to get constructed and occupied
        var point3 = new Point(7, 9);
        var storehouse = map.placeBuilding(new Storehouse(player0), point3.upLeft());
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        var storehouseWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Disconnect the storehouse from the headquarters
        map.removeRoad(road0);

        // Add planks to the storehouse
        Utils.adjustInventoryTo(storehouse, PLANK, 30);

        // Connect the storehouse with woodcutter
        var point2 = new Point(9, 9);
        var road1 = map.placeRoad(player0, point1, point2, point3);

        // The storehouse worker rests
        Utils.fastForward(19, map);

        // The storehouse worker delivers stone or planks to the woodcutter
        assertTrue(storehouseWorker0.isInsideBuilding());

        map.stepTime();

        assertFalse(storehouseWorker0.isInsideBuilding());
        assertNotNull(storehouseWorker0.getCargo());
        assertEquals(storehouseWorker0.getTarget(), storehouse.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(storehouseWorker0, storehouse.getFlag().getPosition());

        // Verify that the storehouse worker goes back to the storehouse
        assertEquals(storehouseWorker0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(storehouseWorker0);

        assertTrue(storehouseWorker0.isInsideBuilding());
    }

    @Test
    public void testStorehouseWorkerRestsInStorehouseAfterDelivery() throws Exception {

        // Create a single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(11, 9);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1.upLeft());

        // Place storehouse and wait for it to get constructed and occupied
        var point3 = new Point(7, 9);
        var storehouse = map.placeBuilding(new Storehouse(player0), point3.upLeft());
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Disconnected the storehouse from the headquarters
        map.removeRoad(road0);

        // Add planks to the storehouse
        Utils.adjustInventoryTo(storehouse, PLANK, 30);

        // Connect the storehouse with the woodcutter
        var road1 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), woodcutter.getFlag());

        // Wait for the storehouse worker to start delivering planks
        var storehouseWorker0 = storehouse.getWorker();
        Utils.fastForwardUntilWorkerCarriesCargo(storehouseWorker0, PLANK);

        assertFalse(storehouseWorker0.isInsideBuilding());
        assertEquals(storehouseWorker0.getTarget(), storehouse.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(storehouseWorker0, storehouse.getFlag().getPosition());

        assertNull(storehouseWorker0.getCargo());

        // Let the storehouse worker go back to the storehouse
        assertEquals(storehouseWorker0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(storehouseWorker0);

        // Verify that the storehouse worker stays in the storehouse and rests
        for (int i = 0; i < 20; i++) {
            assertTrue(storehouseWorker0.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testStorehouseWorkerGoesBackToStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse
        var point26 = new Point(8, 8);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Occupy the storehouse
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        var amount = headquarter0.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(storageWorker, headquarter0.getPosition());

        // Verify that the storehouse worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testStorehouseWorkerDoesNotGoBackToUnfinishedStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(15, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Occupy the storehouse
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        // Place second storehouse
        var point2 = new Point(15, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);

        // Connect the storehouse buildings
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), storehouse1.getFlag());

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the storehouse worker avoids the second storehouse because it's burning, although it's close
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storehouse1.getPosition());
    }

    @Test
    public void testStorehouseWorkerDoesNotGoBackToBurningStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(15, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse and wait for it to get constructed and occupied
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Place second storehouse and wait for it to get constructed and occupied
        var point2 = new Point(15, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, storehouse1.getFlag(), storehouse0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse1);

        // Destroy the second storehouse
        storehouse1.tearDown();

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the storehouse worker avoids the second storehouse because it's burning, although it's close
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storehouse1.getPosition());
    }

    @Test
    public void testStorehouseWorkerDoesNotGoBackToDestroyedStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse and wait for it to get constructed and occupied
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Place second storehouse and wait for it to get constructed and occupied
        var point2 = new Point(15, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);
        var road1 =  map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), storehouse1.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse1);

        // Destroy the second storehouse
        storehouse1.tearDown();

        // Wait for the second storehouse to burn down
        Utils.waitForBuildingToBurnDown(storehouse1);

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the storehouse worker avoids the second storehouse because it's destroyed, although it's close
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storehouse1.getPosition());
    }

    @Test
    public void testStorehouseWorkerDoesNotGoBackOffroadToUnfinishedStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Occupy the storehouse
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        // Place second storehouse
        var point2 = new Point(15, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the storehouse worker avoids the second storehouse because it's burning, although it's close
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storehouse1.getPosition());
    }

    @Test
    public void testStorehouseWorkerDoesNotGoBackOffroadToBurningStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(15, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place the first storehouse, connect it, and wait for it to get constructed and occupied
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Place second storehouse, connect it, and wait for it to get constructed and occupied
        var point2 = new Point(15, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, storehouse1.getFlag(), storehouse0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse1);

        // Destroy the second storehouse
        storehouse1.tearDown();

        // Destroy the first storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the storehouse worker avoids the second storehouse because it's burning, although it's close
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storehouse1.getPosition());
    }

    @Test
    public void testStorehouseWorkerDoesNotGoBackOffroadToDestroyedStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(15, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse and wait for it to get constructed and occupied
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Place second storehouse and wait for it to get constructed and occupied
        var point2 = new Point(15, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, storehouse1.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse1);

        // Remove the roads
        map.removeRoad(road0);
        map.removeRoad(road1);

        // Destroy the second storehouse
        storehouse1.tearDown();

        // Wait for the second storehouse to burn down
        Utils.waitForBuildingToBurnDown(storehouse1);

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the storehouse worker avoids the second storehouse because it's destroyed, although it's close
        assertFalse(storageWorker.isInsideBuilding());
        assertNotEquals(storageWorker.getTarget(), storehouse1.getPosition());
    }

    @Test
    public void testStorehouseWorkerGoesBackOnToStorehouseOnRoadsIfPossibleWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse
        var point26 = new Point(8, 8);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        // Connect the storehouse with the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Occupy the storehouse
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        // Verify that the worker plans to use the roads
        var firstStep = true;
        for (var p : storageWorker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(p));
        }
    }

    @Test
    public void testDestroyedStorehouseIsRemovedAfterSomeTime() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse
        var point26 = new Point(8, 8);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        // Connect the storehouse with the headquarters
        map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Destroy the storehouse
        storehouse0.tearDown();

        assertTrue(storehouse0.isBurningDown());

        // Wait for the storehouse to stop burning
        Utils.fastForward(50, map);

        assertTrue(storehouse0.isDestroyed());

        // Wait for the storehouse to disappear
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), storehouse0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(storehouse0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse
        var point26 = new Point(8, 8);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Remove the flag and verify that the driveway is removed
        assertNotNull(map.getRoad(storehouse0.getPosition(), storehouse0.getFlag().getPosition()));

        map.removeFlag(storehouse0.getFlag());

        assertNull(map.getRoad(storehouse0.getPosition(), storehouse0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse
        var point26 = new Point(8, 8);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Tear down the building and verify that the driveway is removed
        assertNotNull(map.getRoad(storehouse0.getPosition(), storehouse0.getFlag().getPosition()));

        storehouse0.tearDown();

        assertNull(map.getRoad(storehouse0.getPosition(), storehouse0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInStorehouseCannotBeStopped() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(10, 6);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        // Connect the storehouse and the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter.getFlag());

        // Finish the storehouse
        Utils.constructHouse(storehouse0);

        // Assign a worker to the storehouse
        var storehouseWorker = new StorehouseWorker(player0, map);

        Utils.occupyBuilding(storehouseWorker, storehouse0);

        // Verify that production can't be stopped
        try {
            storehouse0.stopProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testProductionInStorehouseCannotBeResumed() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(10, 6);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        // Connect the storehouse and the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter.getFlag());

        // Finish the storehouse
        Utils.constructHouse(storehouse0);

        // Assign a worker to the storehouse
        var storehouseWorker = new StorehouseWorker(player0, map);

        Utils.occupyBuilding(storehouseWorker, storehouse0);

        // Verify that production can't be resumed
        try {
            storehouse0.resumeProduction();

            fail();
        } catch (Exception e) {}
    }

    @Test
    public void testAssignedStorehouseWorkerHasCorrectlySetPlayer() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 51);

        // Place headquarters
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse, connect it, and wait for it to get constructed and occupied
        var point1 = new Point(20, 14);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        var storehouseWorker = Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Verify that the player is set correctly in the worker
        assertEquals(storehouseWorker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorehouseEvenWithoutRoadsAndEnemiesStorehouseIsCloser() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(7, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 2's headquarters
        var point10 = new Point(70, 70);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        // Place player 1's headquarters
        var point1 = new Point(37, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place fortress for player 0
        var point2 = new Point(21, 9);
        var fortress0 = map.placeBuilding(new Fortress(player0), point2);

        // Finish construction of the fortress
        Utils.constructHouse(fortress0);

        // Occupy the fortress
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        // Place storehouse close to the new border
        var point4 = new Point(28, 18);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point4);

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Occupy the storehouse
        var worker = Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        // Verify that the worker goes back to its own storehouse when the fortress is torn down
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testRoadCloseToOpponentGetsPopulatedFromCorrectPlayer() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 101);

        // Place player 0's headquarters
        var point0 = new Point(13, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(45, 17);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place player 2's headquarters
        var point10 = new Point(70, 70);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        // Clear the inventories of soldiers
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter2, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place fortress for player 0
        var point2 = new Point(21, 5);
        var fortress0 = map.placeBuilding(new Fortress(player0), point2);

        // Finish construction of the fortress
        Utils.constructHouse(fortress0);

        // Occupy the fortress
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 9, fortress0);

        // Connect the fortress with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        // Occupy the road
        Utils.occupyRoad(road0);

        // Place barracks close to the new border
        var point4 = new Point(34, 18);
        var barracks0 = map.placeBuilding(new Barracks(player1), point4);

        // Finish construction of the barracks
        Utils.constructHouse(barracks0);

        // Occupy the barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        // Connect the barracks with the headquarters
        var road1 = map.placeAutoSelectedRoad(player1, headquarter1.getFlag(), barracks0.getFlag());

        // Occupy the road
        Utils.occupyRoad(road1);

        // Capture the barracks for player 0
        player0.attack(barracks0, 2, AttackStrength.STRONG);

        // Wait for the attackers to come out
        var attackers = Utils.waitForWorkersOutsideBuilding(Soldier.class, 2, player0);

        var mainAttacker = Utils.getMainAttacker(barracks0, attackers);

        // Wait for the attacker to reach the flag of the barracks
        assertEquals(mainAttacker.getTarget(), barracks0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(mainAttacker, barracks0.getFlag().getPosition());

        // Wait for player 0 to take over the barracks
        assertEquals(mainAttacker.getPosition(), barracks0.getFlag().getPosition());
        assertEquals(mainAttacker.getPlayer(), player0);
        assertEquals(barracks0.getPlayer(), player1);

        var seen = new HashSet<Worker>();

        for (int i = 0; i < 10_000; i++) {
            if (barracks0.getPlayer().equals(player0) && barracks0.getNumberOfHostedSoldiers() > 0) {
                break;
            }

            System.out.println("Soldiers outside");
            for (var w : map.getWorkers()) {
                if (!w.isInsideBuilding() && w.isSoldier()) {
                    System.out.println(w + " -- " + w.getPlayer());

                    seen.add(w);
                }
            }
            System.out.println();


            map.stepTime();
        }

        System.out.println();
        System.out.println("Seen");
        for (var w : seen) {
            System.out.println(w + " -- " + w.getPlayer());
        }

        assertEquals(barracks0.getPlayer(), player0);
        assertTrue(barracks0.getNumberOfHostedSoldiers() > 0);

        // Connect the captured barracks with the headquarters
        var road4 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), fortress0.getFlag());

        // Occupy the road
        Utils.occupyRoad(road4);

        // Place flag
        var point5 = new Point(32, 18);
        var flag0 = map.placeFlag(player0, point5);

        // Place road
        var road3 = map.placeAutoSelectedRoad(player0, flag0, barracks0.getFlag());

        // Verify that player 1's headquarters is closer to the road
        for (var point : road3.getWayPoints()) {

            assertTrue(point.distance(headquarter1.getPosition()) < point.distance(headquarter0.getPosition()));
        }

        // Verify that the barracks gets populated from the right headquartersonly
        var player0Couriers = Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player0).size();
        var player1Couriers = Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player1).size();

        for (int i = 0; i < 1000; i++) {
            var courier = road3.getCourier();

            if (courier != null && road3.getWayPoints().contains(courier.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(road3.getCourier());
        assertTrue(road3.getWayPoints().contains(road3.getCourier().getPosition()));
        assertEquals(road3.getCourier().getPlayer(), player0);
        assertEquals(Utils.findWorkersOfTypeOutsideForPlayer(Courier.class, player0).size(), player0Couriers + 1);
    }

    @Test
    public void testBuilderWorkerReturnsToHeadquartersIfStorehouseIsDestroyed() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse and connect it to the headquarters
        var point2 = new Point(14, 4);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2.upLeft());
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Wait for a builder to come out
        var builder = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        // Wait for the storehouse to get constructed
        Utils.waitForBuildingToBeConstructed(storehouse0);

        // Wait for the builder to be walking to the flag
        Utils.waitForWorkerToHavePointNext(builder, storehouse0.getFlag().getPosition());

        assertEquals(storehouse0.getFlag().getPosition(), builder.getNextPoint());

        // Tear down the storehouse
        storehouse0.tearDown();

        // Verify that the builder goes to the flag, then returns to the headquarters
        Utils.fastForwardUntilWorkerReachesPoint(builder, storehouse0.getFlag().getPosition());

        assertEquals(headquarter0.getPosition(), builder.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(builder, headquarter0.getPosition());

        assertFalse(map.getWorkers().contains(builder));
    }

    @Test
    public void testStorehouseWorkerGoesOffroadBackToClosestStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse and wait for it to get constructed and occupied
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Place a second storehouse closer to the storehouse and wait for it to get constructed and occupied
        var point2 = new Point(13, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, storehouse1.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse1);

        // Remove the roads
        map.removeRoad(road0);
        map.removeRoad(road1);

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), storehouse1.getPosition());

        var amount = storehouse1.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(storageWorker, storehouse1.getPosition());

        // Verify that the storehouse worker is stored correctly in the headquarters
        assertEquals(storehouse1.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testStorehouseWorkerReturnsOffroadAndAvoidsBurningStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse and wait for it to get constructed and occupied
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Place a second storehouse closer to the storehouse, and wait for it to get constructed and occupied
        var point2 = new Point(13, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, storehouse1.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse1);

        // Remove the roads
        map.removeRoad(road0);
        map.removeRoad(road1);

        // Destroy the second storehouse
        storehouse1.tearDown();

        // Destroy the first storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        var amount = headquarter0.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(storageWorker, headquarter0.getPosition());

        // Verify that the storehouse worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testStorehouseWorkerReturnsOffroadAndAvoidsDestroyedStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse and wait for it to get constructed and occupied
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse0);

        // Place a second storehouse closer to the storehouse, and wait for it to get constructed and occupied
        var point2 = new Point(13, 15);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);
        var road1 = map.placeAutoSelectedRoad(player0, storehouse1.getFlag(), storehouse0.getFlag());

        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse1);

        // Remove the roads to the storehouses
        map.removeRoad(road0);
        map.removeRoad(road1);

        // Destroy the second storehouse
        storehouse1.tearDown();

        // Wait for it to burn down
        Utils.waitForBuildingToBurnDown(storehouse1);

        // Destroy the first storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        var amount = headquarter0.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(storageWorker, headquarter0.getPosition());

        // Verify that the storehouse worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testStorehouseWorkerReturnsOffroadAndAvoidsUnfinishedStorehouseWhenStorehouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(15, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Occupy the storehouse
        Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        // Place a second storehouse closer to the storehouse
        var point2 = new Point(13, 13);
        var storehouse1 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the storehouse
        var storageWorker = storehouse0.getWorker();

        assertTrue(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getPosition(), storehouse0.getPosition());

        storehouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(storageWorker.isInsideBuilding());
        assertEquals(storageWorker.getTarget(), headquarter0.getPosition());

        var amount = headquarter0.getAmount(STOREHOUSE_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(storageWorker, headquarter0.getPosition());

        // Verify that the storehouse worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(STOREHOUSE_WORKER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place storehouse and connect it to the headquarters
        var point26 = new Point(17, 17);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point26);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse0.getFlag(), headquarter0.getFlag());

        // Wait for a builder to start working on the storehouse
        var builder = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        // Wait for the storehouse to get fully constructed
        Utils.waitForBuildingToBeConstructed(storehouse0);

        // Wait for the builder to get to the storehouse's flag
        Utils.fastForwardUntilWorkerReachesPoint(builder, storehouse0.getFlag().getPosition());

        // Let the builder start walking to the storehouse
        map.stepTime();

        // Tear down the storehouse
        storehouse0.tearDown();

        // Verify that the builder goes to the storehouse, and then goes to the headquarters instead of entering
        assertEquals(storehouse0.getPosition(), builder.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(builder, storehouse0.getPosition());

        assertFalse(builder.isInsideBuilding());
        assertEquals(headquarter0.getPosition(), builder.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(builder, headquarter0.getPosition());
    }

    @Test
    public void testStorehouseCannotProduce() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(10, 10);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        // Finish construction of the storehouse
        Utils.constructHouse(storehouse0);

        // Populate the storehouse
        var storageWorker0 = Utils.occupyBuilding(new StorehouseWorker(player0, map), storehouse0);

        // Verify that the storehouse can produce
        assertFalse(storehouse0.canProduce());
    }

    @Test
    public void testStorehouseReportsCorrectOutput() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(6, 12);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        // Construct the storehouse
        Utils.constructHouse(storehouse0);

        // Verify that the reported output is correct
        assertEquals(storehouse0.getProducedMaterial().length, 0);
    }

    @Test
    public void testStorehouseReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(6, 12);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(storehouse0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(storehouse0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(storehouse0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(storehouse0.getCanHoldAmount(PLANK), 4);
        assertEquals(storehouse0.getCanHoldAmount(STONE), 3);

        for (var material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(storehouse0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testStorehouseReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 41);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(6, 12);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point1);

        // Construct the storehouse
        Utils.constructHouse(storehouse0);

        // Verify that the reported needed construction material is correct
        assertEquals(storehouse0.getTypesOfMaterialNeeded().size(), 0);

        for (var material : Material.values()) {
            assertEquals(storehouse0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testStorehouseWaitsWhenFlagIsFull() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Place storehouse
        var point1 = new Point(16, 6);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Wait for the storehouse to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(storehouse, PLANK, 50);
        Utils.adjustInventoryTo(storehouse, STONE, 50);

        // Fill the flag with flour cargos
        Utils.placeCargos(FLOUR, 8, storehouse.getFlag(), headquarter);

        // Block storehouse of flour to keep the flag filled up
        storehouse.blockDeliveryOfMaterial(FLOUR);

        // Remove the road
        map.removeRoad(road0);

        // Place fortress
        var point2 = new Point(12, 10);
        var fortress = map.placeBuilding(new Fortress(player0), point2);

        // Connect the fortress with the storehouse
        var road1 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), storehouse.getFlag());

        // Verify that the storehouse waits for the flag to get empty and produces nothing
        for (int i = 0; i < 300; i++) {
            assertEquals(storehouse.getFlag().getStackedCargo().size(), 8);
            assertNull(storehouse.getWorker().getCargo());

            map.stepTime();
        }

        // Remove one of the cargos
        var cargo = storehouse.getFlag().getStackedCargo().getFirst();
        storehouse.getFlag().retrieveCargo(cargo);

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 7);

        // Verify that the worker produces a cargo of flour and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(storehouse.getWorker());
    }

    @Test
    public void testStorehouseDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Place storehouse and connect it to the headquarters
        var point1 = new Point(16, 6);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Wait for the storehouse to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Make sure there is enough construction material in the storehouse
        Utils.adjustInventoryTo(storehouse, PLANK, 50);
        Utils.adjustInventoryTo(storehouse, STONE, 50);

        // Fill the flag with cargos
        Utils.placeCargos(FLOUR, 8, storehouse.getFlag(), headquarter);

        // Block storehouse of flour in the storehouse to keep the flag filled up
        storehouse.blockDeliveryOfMaterial(FLOUR);

        // Remove the road
        map.removeRoad(road0);

        // Place fortress
        var point2 = new Point(12, 10);
        var fortress = map.placeBuilding(new Fortress(player0), point2);

        // Connect the fortress with the storehouse
        var road1 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), storehouse.getFlag());

        // The storehouse waits for the flag to get empty and produces nothing
        for (int i = 0; i < 300; i++) {
            assertEquals(storehouse.getFlag().getStackedCargo().size(), 8);
            assertNull(storehouse.getWorker().getCargo());

            map.stepTime();
        }

        // Remove a cargo from the flag
        var cargo = storehouse.getFlag().getStackedCargo().getFirst();
        storehouse.getFlag().retrieveCargo(cargo);

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 7);

        // Wait for the worker to put the cargo on the flag
        assertTrue(fortress.needsMaterial(PLANK));
        assertTrue(storehouse.getAmount(PLANK) > 0);

        var newCargo = Utils.fastForwardUntilWorkerCarriesCargo(storehouse.getWorker(), () -> System.out.println(storehouse.getWorker()));

        assertEquals(storehouse.getWorker().getTarget(), storehouse.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(storehouse.getWorker(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 8);

        // Verify that the storehouse doesn't produce anything because the flag is full until the courier comes and removes a cargo
        for (int i = 0; i < 400; i++) {
            if (storehouse.getFlag().getStackedCargo().size() < 8) {
                break;
            }

            assertEquals(storehouse.getFlag().getStackedCargo().size(), 8);
            assertNull(storehouse.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testPushedOutCargoGoesToOtherStorehouse() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(16, 6);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Wait for the storehouse to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Push out fish from the headquarters
        Utils.adjustInventoryTo(headquarter, FISH, 10);

        headquarter.pushOutAll(FISH);

        // Verify that all the fish gets transported to the storehouse
        assertEquals(storehouse.getAmount(FISH), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(headquarter.getWorker(), FISH);

        assertEquals(headquarter.getWorker().getCargo().getMaterial(), FISH);

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, FISH, 0);

        assertEquals(headquarter.getAmount(FISH), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(storehouse, FISH, 10);

        assertEquals(storehouse.getAmount(FISH), 10);
    }

    @Test
    public void testStopPushingOutCargo() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(16, 6);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Wait for the storehouse to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Push out fish from the headquarters
        Utils.adjustInventoryTo(headquarter, FISH, 10);

        headquarter.pushOutAll(FISH);

        // Wait for two fishes to get pushed
        assertEquals(headquarter.getFlag().getStackedCargo().size(), 0);

        var headquarterWorker = headquarter.getWorker();

        Utils.fastForwardUntilWorkerCarriesCargo(headquarterWorker, FISH);

        Utils.fastForwardUntilWorkerCarriesNoCargo(headquarterWorker);

        Utils.fastForwardUntilWorkerCarriesCargo(headquarterWorker, FISH);

        // Verify that no more fish is pushed out when pushing out is stopped
        headquarter.stopPushingOut(FISH);

        Utils.fastForwardUntilWorkerCarriesNoCargo(headquarterWorker);

        for (int i = 0; i < 2_000; i++) {
            assertNull(headquarterWorker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testPushedOutWorkerGoesToOtherStorehouseWhenOwnStoreIsBlocked() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(16, 6);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Wait for the storehouse to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Push out fish from the headquarters
        Utils.adjustInventoryTo(headquarter, SCOUT, 10);

        headquarter.pushOutAll(SCOUT);
        headquarter.blockDeliveryOfMaterial(SCOUT);

        // Verify that all the scout goes to the storehouse
        assertEquals(storehouse.getAmount(SCOUT), 0);

        var scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).getFirst();

        assertEquals(scout.getPosition(), headquarter.getPosition());
        assertNull(headquarter.getWorker().getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(scout, headquarter.getFlag().getPosition());

        assertEquals(scout.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(scout, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(scout));

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, SCOUT, 0);

        assertEquals(headquarter.getAmount(SCOUT), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(storehouse, SCOUT, 10);

        assertEquals(storehouse.getAmount(SCOUT), 10);
    }

    @Test
    public void testPushedOutMaterialFollowsPriorityOrder() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(16, 6);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Wait for the storehouse to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(storehouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(storehouse);

        // Push out fish from the headquarters
        Utils.adjustInventoryTo(headquarter, FISH, 10);
        Utils.adjustInventoryTo(headquarter, COIN, 10);

        headquarter.pushOutAll(FISH);
        headquarter.pushOutAll(COIN);

        // Set transport priority for fish above coin
        player0.setTransportPriority(0, TransportCategory.FOOD);
        player0.setTransportPriority(1, TransportCategory.COIN);

        // Verify that all the fish gets transported to the storehouse before the coins
        assertEquals(storehouse.getAmount(FISH), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(headquarter.getWorker(), FISH);

        assertEquals(headquarter.getWorker().getCargo().getMaterial(), FISH);

        Utils.waitForBuildingToGetAmountOfMaterial(headquarter, FISH, 0);

        assertEquals(headquarter.getAmount(FISH), 0);
        assertEquals(headquarter.getAmount(COIN), 10);
    }

    @Test
    public void testDeliveriesGoToOtherStorehouseWhenDeliveryIsBlocked() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(16, 6);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place well
        var point2 = new Point(9, 7);
        var well = map.placeBuilding(new Well(player0), point2);

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Wait for the storehouse to get constructed
        Utils.waitForBuildingToBeConstructed(storehouse);

        // Connect the well with the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        // Wait for the well to get constructed
        Utils.waitForBuildingToBeConstructed(well);

        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        /* Verify that when delivery is blocked for water in the headquarter,
           all deliveries from the well go to the storehouse even if it's further away
        */
        assertTrue(well.isReady());
        assertNotNull(well.getWorker());

        headquarter.blockDeliveryOfMaterial(WATER);

        Utils.adjustInventoryTo(storehouse, WATER, 0);

        for (int i = 0; i < 10; i++) {

            // Wait for the well worker to produce a water cargo
            var cargo = Utils.fastForwardUntilWorkerCarriesCargo(well.getWorker(), WATER);

            // Wait for the courier for the road between the well and the headquarters to pick up the water cargo
            Utils.fastForwardUntilWorkerCarriesCargo(road1.getCourier(), cargo);

            assertEquals(road1.getCourier().getTarget(), headquarter.getFlag().getPosition());

            /* Verify that the cargo is put on the headquarters' flag and picked up by the second courier,
               instead of delivered to the headquarters
             */
            Utils.fastForwardUntilWorkerReachesPoint(road1.getCourier(), headquarter.getFlag().getPosition());

            assertTrue(headquarter.getFlag().getStackedCargo().contains(cargo));

            Utils.fastForwardUntilWorkerCarriesCargo(road0.getCourier(), cargo);

            assertEquals(road0.getCourier().getTarget(), storehouse.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(road0.getCourier(), storehouse.getPosition());

            assertNull(road0.getCourier().getCargo());
            assertEquals(storehouse.getAmount(WATER), i + 1);
        }
    }

    @Test
    public void testAllowingDeliveriesAgainAfterBeingBlocked() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(16, 6);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place well
        var point2 = new Point(9, 7);
        var well = map.placeBuilding(new Well(player0), point2);

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Wait for the storehouse to get constructed
        Utils.waitForBuildingToBeConstructed(storehouse);

        // Connect the well with the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, well.getFlag(), headquarter.getFlag());

        // Wait for the well to get constructed
        Utils.waitForBuildingToBeConstructed(well);

        Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        // Block delivery of water in the headquarters. All deliveries from the well go to the storehouse even if it's further away
        assertTrue(well.isReady());
        assertNotNull(well.getWorker());

        headquarter.blockDeliveryOfMaterial(WATER);

        Utils.adjustInventoryTo(storehouse, WATER, 0);

        // Wait for the well worker to produce a water cargo
        var cargo = Utils.fastForwardUntilWorkerCarriesCargo(well.getWorker(), WATER);

        // Wait for the courier for the road between the well and the headquarters to pick up the water cargo
        Utils.fastForwardUntilWorkerCarriesCargo(road1.getCourier(), cargo);

        assertEquals(road1.getCourier().getTarget(), headquarter.getFlag().getPosition());

        // The cargo is put on the headquarters' flag and picked up by the second courier, instead of delivered to the headquarters
        Utils.fastForwardUntilWorkerReachesPoint(road1.getCourier(), headquarter.getFlag().getPosition());

        assertTrue(headquarter.getFlag().getStackedCargo().contains(cargo));

        Utils.fastForwardUntilWorkerCarriesCargo(road0.getCourier(), cargo);

        assertEquals(road0.getCourier().getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(road0.getCourier(), storehouse.getPosition());

        assertNull(road0.getCourier().getCargo());
        assertEquals(storehouse.getAmount(WATER), 1);

        // Verify that deliveries go to the headquarters again when they are allowed
        headquarter.allowDeliveryOfMaterial(WATER);

        Utils.fastForwardUntilWorkerCarriesCargo(road1.getCourier(), WATER);

        assertEquals(headquarter.getAmount(WATER), 0);
        assertEquals(road1.getCourier().getTarget(), headquarter.getPosition());
        assertEquals(road1.getCourier().getCargo().getTarget(), headquarter);

        Utils.fastForwardUntilWorkerReachesPoint(road1.getCourier(), headquarter.getPosition());

        assertEquals(headquarter.getAmount(WATER), 1);
        assertNull(road1.getCourier().getCargo());
    }

    @Test
    public void testPushedOutMaterialStopsWhenFlagFillsUp() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 21);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Make sure there is enough construction material in the headquarters
        Utils.adjustInventoryTo(headquarter, PLANK, 50);
        Utils.adjustInventoryTo(headquarter, STONE, 50);

        // Verify that pushing out planks will fill up the flag and then stop
        assertEquals(headquarter.getFlag().getStackedCargo().size(), 0);

        headquarter.pushOutAll(PLANK);
        headquarter.blockDeliveryOfMaterial(PLANK);

        Utils.waitForFlagToGetStackedCargo(headquarter.getFlag(), 8);

        assertEquals(headquarter.getFlag().getStackedCargo().size(), 8);
        assertEquals(headquarter.getWorker().getTarget(), headquarter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(headquarter.getWorker(), headquarter.getPosition());

        for (int i = 0; i < 200; i++) {
            assertTrue(headquarter.getWorker().isInsideBuilding());
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }
    }
    @Test
    public void testStorehouseWorkerReturnsCargoIfItIsStillOnTheFlagAndItsTargetBecomesUnreachable() throws InvalidUserActionException {

        // Creating new game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 101);

        // Place headquarters
        var point0 = new Point(5, 7);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(10, 6);
        var flag0 = map.placeFlag(player0, point1);

        // Place flag
        var point2 = new Point(14, 8);
        var flag1 = map.placeFlag(player0, point2);

        // Place road between the headquarters and the flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Place road between the flag and the woodcutter
        var road1 = map.placeAutoSelectedRoad(player0, flag0, flag1);

        // Wait for the first road to get assigned a courier
        var courier = Utils.waitForRoadToGetAssignedCourier(road0);

        Utils.fastForwardUntilWorkerReachesPoint(courier, flag0.getPosition().left());

        assertEquals(courier.getPosition(), flag0.getPosition().left());

        // Set the amount of planks
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);

        // Place woodcutter
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2.upLeft());

        // Wait for the courier to start walking to the headquarters' flag to pick up a cargo for the woodcutter
        Utils.waitForWorkerToSetTarget(courier, headquarter0.getFlag().getPosition());

        assertEquals(headquarter0.getAmount(PLANK), 19);
        assertNull(courier.getCargo());

        map.stepTime();

        // Remove the second road
        map.removeRoad(road1);

        // Verify that the storehouse worker brings the cargo back to the storehouse
        var storehouseWorker = (StorehouseWorker) headquarter0.getWorker();

        Utils.waitForWorkerToSetTarget(storehouseWorker, headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(storehouseWorker, headquarter0.getFlag().getPosition());

        assertNotNull(storehouseWorker.getCargo());
        assertEquals(storehouseWorker.getCargo().getMaterial(), PLANK);
        assertEquals(storehouseWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(storehouseWorker, headquarter0.getPosition());

        assertNull(storehouseWorker.getCargo());
        assertEquals(headquarter0.getAmount(PLANK), 20);
    }
}
