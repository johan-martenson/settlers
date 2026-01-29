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
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.GoldMine;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.SlaughterHouse;
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

public class TestSlaughterHouse {

    @Test
    public void testSlaughterHouseOnlyNeedsTwoPlanksAndTwoStonesForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place slaughterhouse
        var point22 = new Point(6, 12);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point22);

        // Deliver two plank and two stone
        Utils.deliverCargo(slaughterHouse0, PLANK);
        Utils.deliverCargo(slaughterHouse0, PLANK);
        Utils.deliverCargo(slaughterHouse0, STONE);
        Utils.deliverCargo(slaughterHouse0, STONE);

        // Assign builder
        Utils.assignBuilder(slaughterHouse0);

        // Verify that this is enough to construct the slaughterhouse
        for (int i = 0; i < 150; i++) {
            assertTrue(slaughterHouse0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(slaughterHouse0.isReady());
    }

    @Test
    public void testSlaughterHouseCannotBeConstructedWithTooFewPlanks() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place slaughterhouse
        var point22 = new Point(6, 12);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point22);

        // Deliver one plank and two stone
        Utils.deliverCargo(slaughterHouse0, PLANK);
        Utils.deliverCargo(slaughterHouse0, STONE);
        Utils.deliverCargo(slaughterHouse0, STONE);

        // Assign builder
        Utils.assignBuilder(slaughterHouse0);

        // Verify that this is not enough to construct the slaughterhouse
        for (int i = 0; i < 500; i++) {
            assertTrue(slaughterHouse0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(slaughterHouse0.isReady());
    }

    @Test
    public void testSlaughterHouseCannotBeConstructedWithTooFewStones() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place slaughterhouse
        var point22 = new Point(6, 12);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point22);

        // Deliver two planks and one stones
        Utils.deliverCargo(slaughterHouse0, PLANK);
        Utils.deliverCargo(slaughterHouse0, PLANK);
        Utils.deliverCargo(slaughterHouse0, STONE);

        // Assign builder
        Utils.assignBuilder(slaughterHouse0);

        // Verify that this is not enough to construct the slaughterhouse
        for (int i = 0; i < 500; i++) {
            assertTrue(slaughterHouse0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(slaughterHouse0.isReady());
    }

    @Test
    public void testSlaughterHouseNeedsWorker() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Unfinished slaughterhouse doesn't need worker
        assertFalse(slaughterHouse.needsWorker());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        assertTrue(slaughterHouse.needsWorker());
    }

    @Test
    public void testHeadquarterAtLeastHasOneButcherAtStart() {
        var headquarter = new Headquarter(null);

        assertTrue(headquarter.getAmount(BUTCHER) >= 1);
    }

    @Test
    public void testSlaughterHouseGetsAssignedWorker() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        assertTrue(slaughterHouse.needsWorker());

        // Verify that a slaughterhouse worker leaves the headquarters
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

        // Let the slaughterhouse worker reach the slaughterhouse
        Butcher butcher = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher)worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, butcher);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);
    }

    @Test
    public void testButcherIsNotASoldier() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        assertTrue(slaughterHouse.needsWorker());

        // Wait for a butcher to walk out
        var butcher0 = Utils.waitForWorkerOutsideBuilding(Butcher.class, player0);

        // Verify that the butcher is not a soldier
        assertFalse(butcher0.isSoldier());
    }

    @Test
    public void testButcherGetsCreatedFromCleaver() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Remove all butchers from the headquarters and add one cleaver
        Utils.adjustInventoryTo(headquarter, BUTCHER, 0);
        Utils.adjustInventoryTo(headquarter, Material.CLEAVER, 1);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        assertTrue(slaughterHouse.needsWorker());

        // Verify that a slaughterhouse worker leaves the headquarters
        Utils.fastForward(3, map);

        assertTrue(map.getWorkers().size() >= 3);

        // Let the slaughterhouse worker reach the slaughterhouse
        Butcher butcher = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher)worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, butcher);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);
    }

    @Test
    public void testOccupiedSlaughterHouseWithoutPigsProducesNothing() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        // Populate the slaughterhouse
        var butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);

        // Verify that the slaughterhouse doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
            assertFalse(slaughterHouse.isWorking());

            map.stepTime();
        }
    }

    @Test
    public void testUnoccupiedSlaughterHouseProducesNothing() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        // Verify that the slaughterhouse doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertFalse(slaughterHouse.isWorking());

            map.stepTime();
        }
    }

    @Test
    public void testOccupiedSlaughterHouseWithPigsProducesMeat() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        // Populate the slaughterhouse
        var butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);
        assertFalse(slaughterHouse.isWorking());

        // Deliver pig to the slaughterhouse
        slaughterHouse.putCargo(new Cargo(PIG, map));

        // Let the butcher rest
        for (int i = 0; i < 99; i++) {
            map.stepTime();

            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
            assertFalse(slaughterHouse.isWorking());
            assertFalse(butcher.isSlaughtering());
        }

        // Verify that the slaughterhouse produces meat
        assertFalse(slaughterHouse.isWorking());

        map.stepTime();

        for (int i = 0; i < 49; i++) {
            assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher.getCargo());
            assertTrue(slaughterHouse.isWorking());
            assertTrue(butcher.isSlaughtering());

            map.stepTime();
        }

        map.stepTime();

        assertNotNull(butcher.getCargo());
        assertEquals(butcher.getCargo().getMaterial(), MEAT);
        assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());
        assertFalse(slaughterHouse.isWorking());
        assertFalse(butcher.isSlaughtering());
    }

    @Test
    public void testButcherLeavesMeatAtTheFlag() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        // Populate the slaughterhouse
        var butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);

        // Deliver ingredients to the slaughterhouse
        Utils.deliverCargo(slaughterHouse, PIG);

        // Wait for the slaughterhouse to produce meat
        Utils.fastForwardUntilWorkerCarriesCargo(map, butcher);

        assertEquals(butcher.getCargo().getMaterial(), MEAT);
        assertTrue(slaughterHouse.getFlag().getStackedCargo().isEmpty());

        // Verify that the slaughterhouse worker leaves the cargo at the flag
        assertEquals(butcher.getTarget(), slaughterHouse.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, slaughterHouse.getFlag().getPosition());

        assertFalse(slaughterHouse.getFlag().getStackedCargo().isEmpty());
        assertNull(butcher.getCargo());
        assertEquals(butcher.getTarget(), slaughterHouse.getPosition());

        // Verify that the butcher goes back to the slaughterhouse
        Utils.fastForwardUntilWorkersReachTarget(map, butcher);

        assertTrue(butcher.isInsideBuilding());
    }

    @Test
    public void testMeatCargoIsDeliveredToMineWhichIsCloserThanHeadquarters() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Remove all meat from the headquarters
        Utils.adjustInventoryTo(headquarter, MEAT, 0);

        // Place a small mountain
        var point4 = new Point(10, 4);
        Utils.surroundPointWithMinableMountain(point4, map);

        // Place coal mine
        var coalMine = map.placeBuilding(new CoalMine(player0), point4);

        // Connect the coal mine to the headquarters
        var road2 = map.placeAutoSelectedRoad(player0, coalMine.getFlag(), headquarter.getFlag());

        // Wait for the coal mine to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(coalMine);

        Utils.waitForNonMilitaryBuildingToGetPopulated(coalMine);

        // Place the slaughterhouse
        var point1 = new Point(14, 4);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Connect the slaughterhouse with the coal mine
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), coalMine.getFlag());

        // Wait for the slaughterhouse to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(slaughterHouse);

        Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse);

        // Wait for the courier on the road between the coal mine and the slaughterhouse to have a meat cargo
        Utils.deliverCargo(slaughterHouse, PIG);

        Utils.waitForFlagToGetStackedCargo(map, slaughterHouse.getFlag(), 1);

        assertEquals(slaughterHouse.getFlag().getStackedCargo().getFirst().getMaterial(), MEAT);

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that the courier delivers the cargo to the coal mine (and not the headquarters)
        assertEquals(slaughterHouse.getAmount(MEAT), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), coalMine.getPosition());

        assertEquals(coalMine.getAmount(MEAT), 1);
    }

    @Test
    public void testMeatIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Adjust the inventory so that there are stones, planks, or meat
        Utils.adjustInventoryTo(headquarter, PLANK, 0);
        Utils.adjustInventoryTo(headquarter, STONE, 0);
        Utils.adjustInventoryTo(headquarter, MEAT, 0);

        // Place storehouse
        var point4 = new Point(10, 4);
        var storehouse = map.placeBuilding(new Storehouse(player0), point4);

        // Connect the storehouse to the headquarters
        var road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Place the slaughterhouse
        var point1 = new Point(14, 4);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Connect the slaughterhouse with the storehouse
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), storehouse.getFlag());

        // Deliver the needed planks to the slaughterhouse
        Utils.deliverCargos(slaughterHouse, PLANK, 2);
        Utils.deliverCargos(slaughterHouse, STONE, 2);

        // Wait for the slaughterhouse to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(slaughterHouse);

        Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse);

        // Wait for the courier on the road between the storehouse and the slaughterhouse hut to have a meat cargo
        Utils.deliverCargo(slaughterHouse, PIG);

        Utils.waitForFlagToGetStackedCargo(map, slaughterHouse.getFlag(), 1);

        assertEquals(slaughterHouse.getFlag().getStackedCargo().getFirst().getMaterial(), MEAT);

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that the courier delivers the cargo to the storehouse's flag so that it can continue to the headquarters
        assertEquals(headquarter.getAmount(MEAT), 0);
        assertEquals(slaughterHouse.getAmount(MEAT), 0);
        assertFalse(storehouse.needsMaterial(MEAT));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(MEAT));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testMeatIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Adjust the inventory so that there is no meat
        Utils.adjustInventoryTo(headquarter, MEAT, 0);

        // Place mountain
        var point2 = new Point(10, 4);
        Utils.surroundPointWithMinableMountain(point2, map);

        // Place mine
        var goldMine = map.placeBuilding(new GoldMine(player0), point2);

        // Construct the gold mine
        Utils.constructHouse(goldMine);

        // Connect the gold mine to the headquarters
        var road2 = map.placeAutoSelectedRoad(player0, goldMine.getFlag(), headquarter.getFlag());

        // Fill up the gold mine so there is only space for one more meat
        Utils.deliverCargo(goldMine, MEAT);

        // Stop production
        goldMine.stopProduction();

        // Place the slaughterhouse
        var point1 = new Point(14, 4);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Connect the slaughterhouse with the gold mine
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), goldMine.getFlag());

        // Deliver the needed planks to construct the slaughterhouse
        Utils.deliverCargos(slaughterHouse, PLANK, 2);
        Utils.deliverCargos(slaughterHouse, STONE, 2);

        // Wait for the slaughterhouse to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(slaughterHouse);

        Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse);

        // Wait for the flag on the road between the gold mine and the slaughterhouse to have a meat cargo
        Utils.deliverCargo(slaughterHouse, PIG);

        Utils.waitForFlagToGetStackedCargo(map, slaughterHouse.getFlag(), 1);

        assertEquals(slaughterHouse.getFlag().getStackedCargo().getFirst().getMaterial(), MEAT);

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that no stone is delivered from the headquarters
        Utils.adjustInventoryTo(headquarter, MEAT, 1);

        assertEquals(goldMine.getCanHoldAmount(MEAT) - goldMine.getAmount(MEAT), 1);
        assertFalse(goldMine.needsMaterial(MEAT));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(MEAT), 1);
    }

    @Test
    public void testProductionOfOneBreadConsumesOnePig() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        // Populate the slaughterhouse
        var butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        // Deliver ingredients to the slaughterhouse
        slaughterHouse.putCargo(new Cargo(PIG, map));

        // Wait until the slaughterhouse worker produces meat
        assertEquals(slaughterHouse.getAmount(PIG), 1);

        Utils.fastForward(150, map);

        assertEquals(slaughterHouse.getAmount(PIG), 0);
    }

    @Test
    public void testProductionCountdownStartsWhenMaterialIsAvailable() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        // Populate the slaughterhouse
        var butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        // Fast forward so that the slaughterhouse var would have produced meat if it had had a pig
        Utils.fastForward(300, map);

        assertNull(butcher.getCargo());

        // Deliver ingredients to the slaughterhouse
        Utils.deliverCargo(slaughterHouse, PIG);

        // Verify that it takes 50 steps for the slaughterhouse worker to produce the meat
        for (int i = 0; i < 50; i++) {
            assertNull(butcher.getCargo());

            map.stepTime();
        }

        assertNotNull(butcher.getCargo());
    }

    @Test
    public void testSlaughterHouseWithoutConnectedStorageKeepsProducing() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(8, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Occupy the slaughterhouse
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Deliver material to the slaughterhouse
        Utils.deliverCargo(slaughterHouse0, PIG);
        Utils.deliverCargo(slaughterHouse0, PIG);

        // Let the butcher rest
        Utils.fastForward(100, map);

        // Wait for the butcher to produce a new meat cargo
        Utils.fastForward(50, map);

        var worker = slaughterHouse0.getWorker();

        assertNotNull(worker.getCargo());

        // Verify that the butcher puts the meat cargo at the flag
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());
        assertTrue(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        // Wait for the worker to go back to the slaughterhouse
        assertEquals(worker.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getPosition());

        // Wait for the worker to rest and produce another cargo
        Utils.fastForward(150, map);

        assertNotNull(worker.getCargo());

        // Verify that the second cargo is put at the flag
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(slaughterHouse0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(8, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Deliver material to the slaughterhouse
        var pigCargo = new Cargo(PIG, map);

        Utils.deliverCargo(slaughterHouse0, PIG);
        Utils.deliverCargo(slaughterHouse0, PIG);

        // Occupy the slaughterhouse
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Let the butcher rest
        Utils.fastForward(100, map);

        // Wait for the butcher to produce a new meat cargo
        Utils.fastForward(50, map);

        var worker = slaughterHouse0.getWorker();

        assertNotNull(worker.getCargo());

        // Verify that the butcher puts the meat cargo at the flag
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());
        assertTrue(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(slaughterHouse0.getFlag().getStackedCargo().isEmpty());

        // Wait to let the cargo remain at the flag without any connection to the storage
        var cargo = slaughterHouse0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), slaughterHouse0.getFlag().getPosition());

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), slaughterHouse0.getFlag());

        // Assign a courier to the road
        var courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        // Wait for the courier to reach the idle point of the road
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), slaughterHouse0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        // Verify that the courier walks to pick up the cargo
        map.stepTime();

        assertEquals(courier.getTarget(), slaughterHouse0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        // Verify that the courier has picked up the cargo
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        // Verify that the courier delivers the cargo to the headquarters
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(MEAT);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        // Verify that the courier has delivered the cargo to the headquarters
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(MEAT), amount + 1);
    }

    @Test
    public void testButcherGoesBackToStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(8, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Occupy the slaughterhouse
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Destroy the slaughterhouse
        var worker = slaughterHouse0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        // Verify that the butcher is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherGoesBackOnToStorageOnRoadsIfPossibleWhenSlaughterHouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(8, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Connect the slaughterhouse with the headquarters
        map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Occupy the slaughterhouse
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Destroy the slaughterhouse
        var worker = slaughterHouse0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        // Verify that the worker plans to use the roads
        boolean firstStep = true;
        for (var point : worker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testDestroyedSlaughterHouseIsRemovedAfterSomeTime() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(8, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Connect the slaughterhouse with the headquarters
        map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Destroy the slaughterhouse
        slaughterHouse0.tearDown();

        assertTrue(slaughterHouse0.isBurningDown());

        // Wait for the slaughterhouse to stop burning
        Utils.fastForward(50, map);

        assertTrue(slaughterHouse0.isDestroyed());

        // Wait for the slaughterhouse to disappear
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), slaughterHouse0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(slaughterHouse0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(8, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Remove the flag and verify that the driveway is removed
        assertNotNull(map.getRoad(slaughterHouse0.getPosition(), slaughterHouse0.getFlag().getPosition()));

        map.removeFlag(slaughterHouse0.getFlag());

        assertNull(map.getRoad(slaughterHouse0.getPosition(), slaughterHouse0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(8, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Tear down the building and verify that the driveway is removed
        assertNotNull(map.getRoad(slaughterHouse0.getPosition(), slaughterHouse0.getFlag().getPosition()));

        slaughterHouse0.tearDown();

        assertNull(map.getRoad(slaughterHouse0.getPosition(), slaughterHouse0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInSlaughterHouseCanBeStopped() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(10, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Connect the slaughterhouse and the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter.getFlag());

        // Finish the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Assign a worker to the slaughterhouse
        var worker = new Butcher(player0, map);

        Utils.occupyBuilding(worker, slaughterHouse0);

        assertTrue(worker.isInsideBuilding());

        // Deliver material to the slaughterhouse
        var pigCargo = new Cargo(PIG, map);

        Utils.deliverCargo(slaughterHouse0, PIG);

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the butcher to produce cargo
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), MEAT);

        // Wait for the worker to deliver the cargo
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        // Stop production and verify that no meat is produced
        slaughterHouse0.stopProduction();

        assertFalse(slaughterHouse0.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInSlaughterHouseCanBeResumed() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(10, 8);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Connect the slaughterhouse and the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter.getFlag());

        // Finish the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Deliver material to the slaughterhouse
        var pigCargo = new Cargo(PIG, map);

        Utils.deliverCargo(slaughterHouse0, PIG);

        // Assign a worker to the slaughterhouse
        var worker = new Butcher(player0, map);

        Utils.occupyBuilding(worker, slaughterHouse0);

        assertTrue(worker.isInsideBuilding());

        // Let the worker rest
        Utils.fastForward(100, map);

        // Deliver pig to the slaughterhouse
        slaughterHouse0.putCargo(new Cargo(PIG, map));

        // Wait for the butcher to produce meat
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), MEAT);

        // Wait for the worker to deliver the cargo
        assertEquals(worker.getTarget(), slaughterHouse0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        // Stop production
        slaughterHouse0.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        // Resume production and verify that the slaughterhouse produces meat again
        slaughterHouse0.resumeProduction();

        assertTrue(slaughterHouse0.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedButcherHasCorrectlySetPlayer() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 50, 50);

        // Place headquarters
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(20, 14);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), slaughterHouse0.getFlag());

        assertTrue(map.arePointsConnectedByRoads(headquarter0.getPosition(), slaughterHouse0.getPosition()));
        assertTrue(headquarter0.getAmount(BUTCHER) > 0);

        // Wait for butcher to get assigned and leave the headquarters
        var workers = Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0);

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

        // Create game map choosing two players
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

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

        // Place slaughterhouse close to the new border
        var point4 = new Point(28, 18);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point4);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Occupy the slaughterhouse
        var worker = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Verify that the worker goes back to its own storage when the fortress is torn down
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testButcherReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place slaughterhouse
        var point2 = new Point(14, 4);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, slaughterHouse0.getFlag());

        // Wait for the butcher to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0);

        Butcher butcher = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher) worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the butcher has started walking
        assertFalse(butcher.isExactlyAtPoint());

        // Remove the next road
        map.removeRoad(road1);

        // Verify that the butcher continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, flag0.getPosition());

        assertEquals(butcher.getPosition(), flag0.getPosition());

        // Verify that the butcher returns to the headquarters when it reaches the flag
        assertEquals(butcher.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getPosition());
    }

    @Test
    public void testButcherContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place slaughterhouse
        var point2 = new Point(14, 4);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, slaughterHouse0.getFlag());

        // Wait for the butcher to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0);

        Butcher butcher = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher) worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the butcher has started walking
        assertFalse(butcher.isExactlyAtPoint());

        // Remove the current road
        map.removeRoad(road0);

        // Verify that the butcher continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, flag0.getPosition());

        assertEquals(butcher.getPosition(), flag0.getPosition());

        // Verify that the butcher continues to the final flag
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, slaughterHouse0.getFlag().getPosition());

        // Verify that the butcher goes out to slaughterhouse instead of going directly back
        assertNotEquals(butcher.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testButcherReturnsToStorageIfSlaughterHouseIsDestroyed() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place slaughterhouse
        var point2 = new Point(14, 4);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point2.upLeft());

        // Connect headquarters and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, slaughterHouse0.getFlag());

        // Wait for the butcher to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0);

        Butcher butcher = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Butcher) {
                butcher = (Butcher) worker;
            }
        }

        assertNotNull(butcher);
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        // Wait for the butcher to reach the first flag
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, flag0.getPosition());

        map.stepTime();

        // See that the butcher has started walking
        assertFalse(butcher.isExactlyAtPoint());

        // Tear down the slaughterhouse
        slaughterHouse0.tearDown();

        // Verify that the butcher continues walking to the next flag
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, slaughterHouse0.getFlag().getPosition());

        assertEquals(butcher.getPosition(), slaughterHouse0.getFlag().getPosition());

        // Verify that the butcher goes back to storage
        assertEquals(butcher.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testButcherGoesOffroadBackToClosestStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(17, 17);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Occupy the slaughterhouse
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Place a second storage closer to the slaughterhouse
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the slaughterhouse
        var butcher = slaughterHouse0.getWorker();

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(butcher.isInsideBuilding());
        assertEquals(butcher.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, storehouse0.getPosition());

        // Verify that the butcher is stored correctly in the headquarters
        assertEquals(storehouse0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherReturnsOffroadAndAvoidsBurningStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(17, 17);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Occupy the slaughterhouse
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Place a second storage closer to the slaughterhouse
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Destroy the slaughterhouse
        var butcher = slaughterHouse0.getWorker();

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(butcher.isInsideBuilding());
        assertEquals(butcher.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getPosition());

        // Verify that the butcher is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherReturnsOffroadAndAvoidsDestroyedStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(17, 17);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Occupy the slaughterhouse
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Place a second storage closer to the slaughterhouse
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Wait for the storage to burn down
        Utils.waitForBuildingToBurnDown(storehouse0);

        // Destroy the slaughterhouse
        var butcher = slaughterHouse0.getWorker();

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(butcher.isInsideBuilding());
        assertEquals(butcher.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getPosition());

        // Verify that the butcher is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testButcherReturnsOffroadAndAvoidsUnfinishedStorageWhenSlaughterHouseIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(17, 17);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Occupy the slaughterhouse
        Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Place a second storage closer to the slaughterhouse
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the slaughterhouse
        var butcher = slaughterHouse0.getWorker();

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getPosition(), slaughterHouse0.getPosition());

        slaughterHouse0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(butcher.isInsideBuilding());
        assertEquals(butcher.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(BUTCHER);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getPosition());

        // Verify that the butcher is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(BUTCHER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place slaughterhouse
        var point26 = new Point(10, 10);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point26);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Place road to connect the headquarters and the slaughterhouse
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), slaughterHouse0.getFlag());

        // Wait for a worker to start walking to the building
        var worker = Utils.waitForWorkersOutsideBuilding(Butcher.class, 1, player0).getFirst();

        // Wait for the worker to get to the building's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        // Tear down the building
        slaughterHouse0.tearDown();

        // Verify that the worker goes to the building and then returns to the headquarters instead of entering
        assertEquals(worker.getTarget(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testSlaughterHouseWithoutResourcesHasZeroProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place butcher
        var point1 = new Point(7, 9);
        var butcher = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Finish construction of the butcher
        Utils.constructHouse(butcher);

        // Populate the butcher
        var butcher0 = Utils.occupyBuilding(new Butcher(player0, map), butcher);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), butcher);
        assertEquals(butcher.getWorker(), butcher0);

        // Verify that the productivity is 0% when the butcher doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(butcher.getFlag().getStackedCargo().isEmpty());
            assertNull(butcher0.getCargo());
            assertEquals(butcher.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testSlaughterHouseWithAbundantResourcesHasFullProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place butcher
        var point1 = new Point(7, 9);
        var butcher = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Finish construction of the butcher
        Utils.constructHouse(butcher);

        // Populate the butcher
        var butcher0 = Utils.occupyBuilding(new Butcher(player0, map), butcher);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), butcher);
        assertEquals(butcher.getWorker(), butcher0);

        // Connect the butcher with the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), butcher.getFlag());

        // Make the butcher produce some meat with full resources available
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (butcher.needsMaterial(PIG) && butcher.getAmount(PIG) < 2) {
                butcher.putCargo(new Cargo(PIG, map));
            }
        }

        // Verify that the productivity is 100% and stays there
        assertEquals(butcher.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (butcher.needsMaterial(PIG) && butcher.getAmount(PIG) < 2) {
                butcher.putCargo(new Cargo(PIG, map));
            }

            assertEquals(butcher.getProductivity(), 100);
        }
    }

    @Test
    public void testSlaughterHouseLosesProductivityWhenResourcesRunOut() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place butcher
        var point1 = new Point(7, 9);
        var butcher = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Finish construction of the butcher
        Utils.constructHouse(butcher);

        // Populate the butcher
        var butcher0 = Utils.occupyBuilding(new Butcher(player0, map), butcher);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), butcher);
        assertEquals(butcher.getWorker(), butcher0);

        // Connect the butcher with the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), butcher.getFlag());

        // Make the butcher produce some meat with full resources available
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (butcher.needsMaterial(PIG) && butcher.getAmount(PIG) < 2) {
                butcher.putCargo(new Cargo(PIG, map));
            }
        }

        // Verify that the productivity goes down when resources run out
        assertEquals(butcher.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(butcher.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedSlaughterHouseHasNoProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place butcher
        var point1 = new Point(7, 9);
        var butcher = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Finish construction of the butcher
        Utils.constructHouse(butcher);

        // Verify that the unoccupied butcher is unproductive
        for (int i = 0; i < 1000; i++) {
            assertEquals(butcher.getProductivity(), 0);

            if (butcher.needsMaterial(PIG) && butcher.getAmount(PIG) < 2) {
                butcher.putCargo(new Cargo(PIG, map));
            }

            map.stepTime();
        }
    }

    @Test
    public void testSlaughterHouseCanProduce() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(10, 10);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Populate the slaughterhouse
        var butcher0 = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse0);

        // Verify that the slaughterhouse can produce
        assertTrue(slaughterHouse0.canProduce());
    }

    @Test
    public void testSlaughterHouseReportsCorrectOutput() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(6, 12);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Construct the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Verify that the reported output is correct
        assertEquals(slaughterHouse0.getProducedMaterial().length, 1);
        assertEquals(slaughterHouse0.getProducedMaterial()[0], MEAT);
    }

    @Test
    public void testSlaughterHouseReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(6, 12);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(slaughterHouse0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(slaughterHouse0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(slaughterHouse0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(slaughterHouse0.getCanHoldAmount(PLANK), 2);
        assertEquals(slaughterHouse0.getCanHoldAmount(STONE), 2);

        for (var material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(slaughterHouse0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testSlaughterHouseReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(6, 12);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Construct the slaughterhouse
        Utils.constructHouse(slaughterHouse0);

        // Verify that the reported needed construction material is correct
        assertEquals(slaughterHouse0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(slaughterHouse0.getTypesOfMaterialNeeded().contains(PIG));
        assertEquals(slaughterHouse0.getCanHoldAmount(PIG), 1);

        for (var material : Material.values()) {
            if (material == PIG) {
                continue;
            }

            assertEquals(slaughterHouse0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testSlaughterHouseWaitsWhenFlagIsFull() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(16, 6);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Wait for the slaughterhouse to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(slaughterHouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse);

        // Give material to the slaughterhouse
        Utils.deliverCargo(slaughterHouse, PIG);
        Utils.deliverCargo(slaughterHouse, PIG);

        // Fill the flag with flour cargos
        Utils.placeCargos(map, FLOUR, 8, slaughterHouse.getFlag(), headquarter);

        // Remove the road
        map.removeRoad(road0);

        // Verify that the slaughterhouse waits for the flag to get empty and produces nothing
        for (int i = 0; i < 300; i++) {
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);
            assertNull(slaughterHouse.getWorker().getCargo());

            map.stepTime();
        }

        // Reconnect the slaughterhouse with the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Wait for the courier to pick up one of the cargos
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(slaughterHouse.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 7);

        // Verify that the worker produces a cargo of flour and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(map, slaughterHouse.getWorker(), MEAT);
    }

    @Test
    public void testSlaughterHouseDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(16, 6);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Wait for the slaughterhouse to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(slaughterHouse);
        Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse);

        // Give material to the slaughterhouse
        Utils.deliverCargo(slaughterHouse, PIG);
        Utils.deliverCargo(slaughterHouse, PIG);
        Utils.deliverCargo(slaughterHouse, PIG);

        // Fill the flag with cargos
        Utils.placeCargos(map, FLOUR, 8, slaughterHouse.getFlag(), headquarter);

        // Remove the road
        map.removeRoad(road0);

        // The slaughterhouse waits for the flag to get empty and produces nothing
        for (int i = 0; i < 300; i++) {
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);
            assertNull(slaughterHouse.getWorker().getCargo());

            map.stepTime();
        }

        // Reconnect the slaughterhouse with the headquarters
        var road1 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter.getFlag());

        // Wait for the courier to pick up one of the cargos
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 500; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(slaughterHouse.getWorker().getCargo());
            assertNull(courier.getCargo());
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 7);

        // Remove the road
        map.removeRoad(road1);

        // The worker produces a cargo and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(map, slaughterHouse.getWorker(), MEAT);

        // Wait for the worker to put the cargo on the flag
        assertEquals(slaughterHouse.getWorker().getTarget(), slaughterHouse.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, slaughterHouse.getWorker(), slaughterHouse.getFlag().getPosition());

        assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);

        // Verify that the slaughterhouse doesn't produce anything because the flag is full
        for (int i = 0; i < 400; i++) {
            assertEquals(slaughterHouse.getFlag().getStackedCargo().size(), 8);
            assertNull(slaughterHouse.getWorker().getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testWhenMeatDeliveryAreBlockedSlaughterHouseFillsUpFlagAndThenStops() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place slaughterhouse
        var point1 = new Point(7, 9);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Place road to connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        // Wait for the slaughterhouse to get constructed and occupied
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(slaughterHouse0);

        var butcher0 = (Butcher) Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse0);

        assertTrue(butcher0.isInsideBuilding());
        assertEquals(butcher0.getHome(), slaughterHouse0);
        assertEquals(slaughterHouse0.getWorker(), butcher0);

        // Add a lot of material to the headquarters for the slaughterhouse to consume
        Utils.adjustInventoryTo(headquarter0, PIG, 40);

        // Block storage of meat
        headquarter0.blockDeliveryOfMaterial(MEAT);

        // Verify that the slaughterhouse puts eight pieces of meat on the flag and then stops
        Utils.waitForFlagToGetStackedCargo(map, slaughterHouse0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher0, slaughterHouse0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(slaughterHouse0.getFlag().getStackedCargo().size(), 8);
            assertTrue(butcher0.isInsideBuilding() || butcher0.isSlaughtering());

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), MEAT);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndSlaughterHouseIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place slaughterhouse
        var point2 = new Point(18, 6);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point2);

        // Place road to connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the slaughterhouse
        var road1 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the slaughterhouse and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, slaughterHouse0);

        // Add a lot of material to the headquarters for the slaughterhouse to consume
        Utils.adjustInventoryTo(headquarter0, PIG, 40);

        // Wait for the slaughterhouse and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, slaughterHouse0);

        var butcher0 = slaughterHouse0.getWorker();

        assertEquals(butcher0.getHome(), slaughterHouse0);
        assertEquals(slaughterHouse0.getWorker(), butcher0);

        // Verify that the worker goes to the storage when the slaughterhouse is torn down
        headquarter0.blockDeliveryOfMaterial(BUTCHER);

        slaughterHouse0.tearDown();

        map.stepTime();

        assertFalse(butcher0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher0, slaughterHouse0.getFlag().getPosition());

        assertEquals(butcher0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, butcher0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(butcher0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndSlaughterHouseIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place slaughterhouse
        var point2 = new Point(18, 6);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point2);

        // Place road to connect the storehouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the slaughterhouse
        var road1 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the slaughterhouse and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, slaughterHouse0);

        // Add a lot of material to the headquarters for the slaughterhouse to consume
        Utils.adjustInventoryTo(headquarter0, PIG, 40);

        // Wait for the slaughterhouse and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, slaughterHouse0);

        var butcher0 = (Butcher) slaughterHouse0.getWorker();

        assertTrue(butcher0.isInsideBuilding() || butcher0.isSlaughtering());
        assertEquals(butcher0.getHome(), slaughterHouse0);
        assertEquals(slaughterHouse0.getWorker(), butcher0);

        // Verify that the worker goes to the storage off-road when the slaughterhouse is torn down
        headquarter0.blockDeliveryOfMaterial(BUTCHER);

        slaughterHouse0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(butcher0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher0, slaughterHouse0.getFlag().getPosition());

        assertEquals(butcher0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(butcher0));
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
        Utils.adjustInventoryTo(headquarter0, BUTCHER, 1);

        assertEquals(headquarter0.getAmount(BUTCHER), 1);

        headquarter0.pushOutAll(BUTCHER);

        for (int i = 0; i < 10; i++) {
            var worker = Utils.waitForWorkerOutsideBuilding(Butcher.class, player0);

            assertEquals(headquarter0.getAmount(BUTCHER), 0);
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
        Utils.adjustInventoryTo(headquarter0, BUTCHER, 1);

        headquarter0.blockDeliveryOfMaterial(BUTCHER);
        headquarter0.pushOutAll(BUTCHER);

        var worker = Utils.waitForWorkerOutsideBuilding(Butcher.class, player0);

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

        // Place slaughterhouse
        var point1 = new Point(7, 9);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Place road to connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the slaughterhouse to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(slaughterHouse0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(slaughterHouse0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarters
         */
       
        headquarter0.blockDeliveryOfMaterial(BUTCHER);

        var worker = slaughterHouse0.getWorker();

        slaughterHouse0.tearDown();

        assertEquals(worker.getPosition(), slaughterHouse0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, slaughterHouse0.getFlag().getPosition());

        assertEquals(worker.getPosition(), slaughterHouse0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), slaughterHouse0.getPosition());
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

        // Place slaughterhouse
        var point1 = new Point(7, 9);
        var slaughterHouse0 = map.placeBuilding(new SlaughterHouse(player0), point1);

        // Place road to connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the slaughterhouse to get constructed
        Utils.waitForBuildingToBeConstructed(slaughterHouse0);

        // Wait for a butcher to start walking to the slaughterhouse
        var butcher = Utils.waitForWorkerOutsideBuilding(Butcher.class, player0);

        // Wait for the butcher to go past the headquarters's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, headquarter0.getFlag().getPosition());

        map.stepTime();

        // Verify that the butcher goes away and dies when the house has been torn down and storage is not possible
        assertEquals(butcher.getTarget(), slaughterHouse0.getPosition());

        headquarter0.blockDeliveryOfMaterial(BUTCHER);

        slaughterHouse0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, slaughterHouse0.getFlag().getPosition());

        assertEquals(butcher.getPosition(), slaughterHouse0.getFlag().getPosition());
        assertNotEquals(butcher.getTarget(), headquarter0.getPosition());
        assertFalse(butcher.isInsideBuilding());
        assertNull(slaughterHouse0.getWorker());
        assertNotNull(butcher.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, butcher, butcher.getTarget());

        var point = butcher.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(butcher.isDead());
            assertEquals(butcher.getPosition(), point);
            assertTrue(map.getWorkers().contains(butcher));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(butcher));
    }
}
