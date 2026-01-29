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
import org.appland.settlers.model.actors.Donkey;
import org.appland.settlers.model.actors.DonkeyBreeder;
import org.appland.settlers.model.buildings.DonkeyFarm;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Storehouse;
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

public class TestDonkeyFarm {

    @Test
    public void testDonkeyFarmCanHoldSixWheatBarsAndSixWater() throws InvalidUserActionException {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(6, 12);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Connect the donkey farm with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        // Make sure the headquarters has enough resources
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(headquarter0, STONE, 20);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 20);
        Utils.adjustInventoryTo(headquarter0, WATER, 20);
        Utils.adjustInventoryTo(headquarter0, BREWER, 20);

        // Wait for the donkey farm to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(donkeyFarm0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyFarm0);

        // Stop production
        donkeyFarm0.stopProduction();

        // Wait for the donkey farm to get six iron bars and six planks
        Utils.waitForBuildingToGetAmountOfMaterial(donkeyFarm0, WHEAT, 6);
        Utils.waitForBuildingToGetAmountOfMaterial(donkeyFarm0, WATER, 6);

        // Verify that the donkey farm doesn't need any more resources and doesn't get any more deliveries
        assertFalse(donkeyFarm0.needsMaterial(WHEAT));
        assertFalse(donkeyFarm0.needsMaterial(WATER));

        for (int i = 0; i < 2000; i++) {
            assertFalse(donkeyFarm0.needsMaterial(WHEAT));
            assertFalse(donkeyFarm0.needsMaterial(WATER));
            assertEquals(donkeyFarm0.getAmount(WHEAT), 6);
            assertEquals(donkeyFarm0.getAmount(WATER), 6);

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyFarmOnlyNeedsThreePlanksAndThreeStonesForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place donkey farm
        var point22 = new Point(6, 12);
        var farm0 = map.placeBuilding(new DonkeyFarm(player0), point22);

        // Deliver three plank and three stone
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(farm0);

        // Verify that this is enough to construct the donkey farm
        for (int i = 0; i < 200; i++) {
            assertTrue(farm0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(farm0.isReady());
    }

    @Test
    public void testDonkeyFarmCannotBeConstructedWithTooFewPlanks() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place donkey farm
        var point22 = new Point(6, 12);
        var farm0 = map.placeBuilding(new DonkeyFarm(player0), point22);

        // Deliver two plank and three stone
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(farm0);

        // Verify that this is not enough to construct the donkey farm
        for (int i = 0; i < 500; i++) {
            assertTrue(farm0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(farm0.isReady());
    }

    @Test
    public void testDonkeyFarmCannotBeConstructedWithTooFewStones() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point21 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place donkey farm
        var point22 = new Point(6, 12);
        var farm0 = map.placeBuilding(new DonkeyFarm(player0), point22);

        // Deliver three planks and two stones
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(plankCargo);
        farm0.putCargo(stoneCargo);
        farm0.putCargo(stoneCargo);

        // Assign builder
        Utils.assignBuilder(farm0);

        // Verify that this is not enough to construct the donkey farm
        for (int i = 0; i < 500; i++) {
            assertTrue(farm0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(farm0.isReady());
    }

    @Test
    public void testUnfinishedDonkeyFarmNeedsNoDonkeyBreeder() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(10, 10);
        var farm = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Assign builder
        Utils.assignBuilder(farm);

        assertTrue(farm.isUnderConstruction());
        assertFalse(farm.needsWorker());
    }

    @Test
    public void testFinishedDonkeyFarmNeedsDonkeyBreeder() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(15, 15);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(10, 10);
        var farm = map.placeBuilding(new DonkeyFarm(player0), point1);

        Utils.constructHouse(farm);

        assertTrue(farm.isReady());
        assertTrue(farm.needsWorker());
    }

    @Test
    public void testDonkeyBreederIsAssignedToFinishedDonkeyFarm() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var farm = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        // Finish the donkey farm
        Utils.constructHouse(farm);

        // Fast forward so the headquarter dispatches a courier and a donkey breeder
        Utils.fastForward(20, map);

        // Verify that there was a donkey breeder added
        Utils.verifyListContainsWorkerOfType(map.getWorkers(), DonkeyBreeder.class);
    }

    @Test
    public void testDonkeyBreederIsNotASoldier() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var farm = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        // Finish the donkey farm
        Utils.constructHouse(farm);

        // Wait for a donkey farmer to walk out
        var donkeyBreeder0 = Utils.waitForWorkerOutsideBuilding(DonkeyBreeder.class, player0);

        assertNotNull(donkeyBreeder0);
        assertFalse(donkeyBreeder0.isSoldier());
    }

    @Test
    public void testDonkeyBreederRestsInDonkeyFarmThenLeaves() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm.getFlag(), headquarter.getFlag());

        // Construct the donkey farm
        Utils.constructHouse(donkeyFarm);

        // Occupy the donkey farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        // Run the game logic 99 times and make sure the donkey breeder stays in the donkey farm
        for (int i = 0; i < 99; i++) {
            assertTrue(donkeyBreeder.isInsideBuilding());

            map.stepTime();
        }

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Step once and make sure the donkey breeder goes out of the donkey farm
        map.stepTime();

        assertFalse(donkeyBreeder.isInsideBuilding());
    }

    @Test
    public void testDonkeyBreederFeedsTheDonkeysWhenItHasResources() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm.getFlag(), headquarter.getFlag());

        // Construct the house
        Utils.constructHouse(donkeyFarm);

        // Deliver wheat and donkey to the farm
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        donkeyFarm.putCargo(wheatCargo);
        donkeyFarm.putCargo(waterCargo);

        // Occupy the donkey farm with a donkey breeder
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Let the donkey breeder rest
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Step once and make sure the donkey breeder goes out of the farm
        map.stepTime();

        assertFalse(donkeyBreeder.isInsideBuilding());

        var point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());

        // Let the donkey breeder reach the spot and start to feed the donkeys
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());

        for (int i = 0; i < 19; i++) {
            assertTrue(donkeyBreeder.isFeeding());

            map.stepTime();
        }

        assertTrue(donkeyBreeder.isFeeding());
        assertFalse(map.isCropAtPoint(point));

        map.stepTime();

        // Verify that the donkey breeder stopped feeding
        assertFalse(donkeyBreeder.isFeeding());
        assertNull(donkeyBreeder.getCargo());
    }

    @Test
    public void testDonkeyBreederReturnsAfterFeeding() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm.getFlag(), headquarter.getFlag());

        // Construct the house
        Utils.constructHouse(donkeyFarm);

        // Assign a donkey breeder to the farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        // Wait for the donkey breeder to rest
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Step once to let the donkey breeder go out to plant
        map.stepTime();

        assertFalse(donkeyBreeder.isInsideBuilding());

        var point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());

        // Let the donkey breeder reach the intended spot and start to feed
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());

        // Wait for the donkey breeder to feed
        Utils.fastForward(19, map);

        assertTrue(donkeyBreeder.isFeeding());

        map.stepTime();

        // Verify that the donkey breeder stopped feeding and is walking back to the farm
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm.getFlag(), headquarter.getFlag());

        Utils.constructHouse(donkeyFarm);

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        // Assign a donkey breeder to the farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Let the donkey breeder rest
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Step once and to let the donkey breeder go out to feed
        map.stepTime();

        assertFalse(donkeyBreeder.isInsideBuilding());

        var point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());

        // Let the donkey breeder reach the intended spot
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());

        // Wait for the donkey breeder to feed the donkeys
        Utils.fastForward(19, map);

        assertTrue(donkeyBreeder.isFeeding());

        map.stepTime();

        // Donkey breeder is walking back to farm without carrying a cargo
        assertFalse(donkeyBreeder.isFeeding());
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm.getPosition());
        assertNull(donkeyBreeder.getCargo());

        // Let the donkey breeder reach the farm
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isInsideBuilding());

        // Wait for the donkey breeder to prepare the donkey
        int amount = map.getWorkers().size();

        Utils.fastForward(20, map);

        // Verify that the donkey walks to the storage by itself and the donkey breeder stays in the farm
        map.stepTime();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(map.getWorkers().size(), amount + 1);
        assertNull(donkeyBreeder.getCargo());

        Donkey donkey = null;
        for (var worker : map.getWorkers()) {
            if (worker instanceof Donkey && worker.getTarget().equals(headquarter.getPosition())) {
                donkey = (Donkey)worker;

                break;
            }
        }

        assertNotNull(donkey);
        assertEquals(donkey.getTarget(), headquarter.getPosition());

        // Verify that the donkey walks to the headquarter
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, headquarter.getPosition());

        assertEquals(donkey.getPosition(), headquarter.getPosition());
    }

    @Test
    public void testDonkeyWalksToMainRoadWhichIsCloserThanHeadquarters() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point3 = new Point(6, 4);
        var headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Remove all donkeys, water and wheat from the headquarters
        Utils.adjustInventoryTo(headquarter, DONKEY, 0);
        Utils.adjustInventoryTo(headquarter, WATER, 0);
        Utils.adjustInventoryTo(headquarter, WHEAT, 0);

        // Place donkey farm
        var point4 = new Point(10, 4);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point4);

        // Connect the donkey farm to the headquarters
        var road2 = map.placeAutoSelectedRoad(player0, donkeyFarm.getFlag(), headquarter.getFlag());

        // Make the road between the donkey farm and the headquarters into a main road
        for (int i = 0; i < 10000; i++) {
            if (road2.isMainRoad()) {
                break;
            }

            if (donkeyFarm.getFlag().getStackedCargo().isEmpty()) {
                Utils.placeCargo(map, PLANK, donkeyFarm.getFlag(), headquarter);
            }

            map.stepTime();
        }

        assertTrue(road2.isMainRoad());

        // Wait for the donkey farm to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(donkeyFarm);

        Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyFarm);

        // Wait for the donkey farm to produce a donkey
        Utils.adjustInventoryTo(headquarter, WATER, 2);
        Utils.adjustInventoryTo(headquarter, WHEAT, 2);

        var donkey = Utils.waitForWorkerOutsideBuilding(Donkey.class, player0);

        // Verify that the donkey goes to the main road and starts working there directly
        assertTrue(road2.isMainRoad());

        for (int i = 0; i < 1000; i++) {
            assertFalse(donkey.getPosition().equals(headquarter.getFlag().getPosition()));

            if (donkey.equals(road2.getDonkey())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(road2.getDonkey(), donkey);
    }

    @Test
    public void testDonkeyFarmWithoutDonkeyBreederProducesNothing() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter.getFlag());

        Utils.constructHouse(donkeyFarm0);

        // Verify that the farm does not produce any donkeys
        boolean newDonkeyFound = false;

        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
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

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(8, 8);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Occupy the donkey farm
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Deliver material to the donkey farm
        Cargo wheatCargo = new Cargo(WHEAT, map);
        Cargo waterCargo = new Cargo(WATER, map);

        donkeyFarm0.putCargo(wheatCargo);
        donkeyFarm0.putCargo(wheatCargo);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(waterCargo);

        // Let the donkey breeder rest
        Utils.fastForward(100, map);

        // Wait for the donkey breeder to produce a new donkey
        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
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

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(8, 8);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Occupy the donkey farm
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Destroy the donkey farm
        var worker = donkeyFarm0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarter
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        // Verify that the donkey breeder is stored correctly in the headquarter
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederGoesBackOnToStorageOnRoadsIfPossibleWhenDonkeyFarmIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(8, 8);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Connect the donkey farm with the headquarter
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Occupy the donkey farm
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Destroy the donkey farm
        var worker = donkeyFarm0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarter
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
    public void testDonkeyBreederWithoutResourcesProducesNothing() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Utils.constructHouse(donkeyFarm);

        // Occupy the donkey farm with a donkey breeder
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Let the donkey breeder rest
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Verify that the donkey breeder doesn't produce anything
        for (int i = 0; i < 300; i++) {
            assertNull(donkeyBreeder.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyBreederWithoutResourcesStaysInHouse() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Utils.constructHouse(donkeyFarm);

        // Occupy the donkey farm with a donkey breeder
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Let the donkey breeder rest
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Verify that the donkey breeder doesn't produce anything
        for (int i = 0; i < 300; i++) {
            assertTrue(donkeyBreeder.isInsideBuilding());
            assertFalse(donkeyFarm.isWorking());

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyBreederFeedsDonkeysWithWaterAndWheat() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        Utils.constructHouse(donkeyFarm);

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        // Assign a donkey breeder to the farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Let the donkey breeder rest
        Utils.fastForward(99, map);

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertFalse(donkeyFarm.isWorking());

        // Step once and to let the donkey breeder go out to feed
        map.stepTime();

        assertFalse(donkeyBreeder.isInsideBuilding());
        assertTrue(donkeyFarm.isWorking());

        var point = donkeyBreeder.getTarget();

        assertTrue(donkeyBreeder.isTraveling());

        // Let the donkey breeder reach the intended spot
        Utils.fastForwardUntilWorkersReachTarget(map, donkeyBreeder);

        assertTrue(donkeyBreeder.isArrived());
        assertTrue(donkeyBreeder.isAt(point));
        assertTrue(donkeyBreeder.isFeeding());

        // Wait for the donkey breeder to feed the donkeys
        Utils.fastForward(19, map);

        assertTrue(donkeyBreeder.isFeeding());

        map.stepTime();

        // Verify that the donkey breeder is done feeding and has consumed the water and wheat
        assertFalse(donkeyBreeder.isFeeding());
        assertEquals(donkeyFarm.getAmount(WATER), 0);
        assertEquals(donkeyFarm.getAmount(WHEAT), 0);
        assertFalse(donkeyFarm.isWorking());
    }

    @Test
    public void testDestroyedDonkeyFarmIsRemovedAfterSomeTime() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(8, 8);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Connect the donkey farm with the headquarter
        map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Destroy the donkey farm
        donkeyFarm0.tearDown();

        assertTrue(donkeyFarm0.isBurningDown());

        // Wait for the donkey farm to stop burning
        Utils.fastForward(50, map);

        assertTrue(donkeyFarm0.isDestroyed());

        // Wait for the donkey farm to disappear
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

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(8, 8);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Remove the flag and verify that the driveway is removed
        assertNotNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));

        map.removeFlag(donkeyFarm0.getFlag());

        assertNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(8, 8);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Tear down the building and verify that the driveway is removed
        assertNotNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));

        donkeyFarm0.tearDown();

        assertNull(map.getRoad(donkeyFarm0.getPosition(), donkeyFarm0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInDonkeyFarmCanBeStopped() throws Exception {

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(12, 8);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Connect the donkey farm and the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter.getFlag());

        // Finish the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Assign a worker to the donkey farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm0);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(wheatCargo);

        // Let the worker rest
        Utils.fastForward(100, map);

        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
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

        // Wait for the new donkey to walk away from the donkey farm
        Utils.fastForward(20, map);

        // Stop production and verify that no donkey is produced
        donkeyFarm0.stopProduction();

        assertFalse(donkeyFarm0.isProductionEnabled());

        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
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

        // Create game map
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(12, 8);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Connect the donkey farm and the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter.getFlag());

        // Finish the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(waterCargo);

        donkeyFarm0.putCargo(wheatCargo);
        donkeyFarm0.putCargo(wheatCargo);

        // Assign a worker to the donkey farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm0);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the donkey breeder to produce donkey
        boolean newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
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

        // Wait for the new donkey to walk away from the donkey farm
        Utils.fastForward(20, map);

        // Stop production
        donkeyFarm0.stopProduction();

        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
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

        // Resume production and verify that the donkey farm produces donkey again
        donkeyFarm0.resumeProduction();

        assertTrue(donkeyFarm0.isProductionEnabled());

        newDonkeyFound = false;
        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
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

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm.getFlag(), headquarter.getFlag());

        Utils.constructHouse(donkeyFarm);

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm.putCargo(waterCargo);
        donkeyFarm.putCargo(wheatCargo);

        // Assign a donkey breeder to the farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Verify that the donkey breeder does not pick up any cargo
        for (int i = 0; i < 500; i++) {
            assertNull(donkeyBreeder.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyWalksToStorageOnExistingRoads() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter.getFlag());

        Utils.constructHouse(donkeyFarm0);

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(wheatCargo);

        // Assign a donkey breeder to the farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm0);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Wait for the donkey farm to create a donkey
        Donkey donkey = null;

        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
                    donkey = (Donkey)worker;

                    break;
                }

            }

            if (donkey != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(donkey);

        // Verify that the donkey walks to the storage on existing roads
        Point previous = null;

        for (var point : donkey.getPlannedPath()) {
            if (previous == null) {
                previous = point;

                continue;
            }

            if (!map.isFlagAtPoint(point)) {
                continue;
            }

            assertNotNull(map.getRoad(previous, point));

            previous = point;
        }
    }

    @Test
    public void testProducedDonkeyIsOnlyAddedOnce() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 20, 20);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point3 = new Point(10, 6);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point3);

        // Place road
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter.getFlag());

        Utils.constructHouse(donkeyFarm0);

        // Deliver resources to the donkey farm
        Cargo waterCargo = new Cargo(WATER, map);
        Cargo wheatCargo = new Cargo(WHEAT, map);

        donkeyFarm0.putCargo(waterCargo);
        donkeyFarm0.putCargo(wheatCargo);

        // Assign a donkey breeder to the farm
        var donkeyBreeder = new DonkeyBreeder(player0, map);

        Utils.occupyBuilding(donkeyBreeder, donkeyFarm0);

        assertTrue(donkeyBreeder.isInsideBuilding());

        // Wait for the donkey farm to create a donkey
        Donkey donkey = null;

        for (int i = 0; i < 500; i++) {
            for (var worker : map.getWorkers()) {
                if (worker instanceof Donkey && worker.getPosition().equals(donkeyFarm0.getPosition())) {
                    donkey = (Donkey)worker;

                    break;
                }

            }

            if (donkey != null) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(donkey);

        // Verify that the donkey is only added once
        for (int i = 0; i < 600; i++) {
            assertEquals(map.getWorkers().indexOf(donkey), map.getWorkers().lastIndexOf(donkey));

            map.stepTime();
        }

    }

    @Test
    public void testAssignedDonkeyBreederHasCorrectlySetPlayer() throws Exception {

        // Create players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        var map = new GameMap(players, 50, 50);

        // Place headquarter
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(20, 14);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Connect the donkey farm with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), donkeyFarm0.getFlag());

        // Wait for donkey breeder to get assigned and leave the headquarter
        List<DonkeyBreeder> workers = Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        // Verify that the player is set correctly in the worker
        DonkeyBreeder worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        // Create game map choosing two players
        var map = new GameMap(players, 100, 100);

        // Place player 2's headquarter
        var point10 = new Point(70, 70);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        // Place player 0's headquarter
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarter
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place fortress for player 0
        var point2 = new Point(21, 9);
        var fortress0 = map.placeBuilding(new Fortress(player0), point2);

        // Finish construction of the fortress
        Utils.constructHouse(fortress0);

        // Occupy the fortress
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        // Place donkey farm close to the new border
        var point4 = new Point(28, 18);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point4);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Occupy the donkey farm
        DonkeyBreeder worker = Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Verify that the worker goes back to its own storage when the fortress is torn down
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testDonkeyBreederReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place donkey farm
        var point2 = new Point(14, 4);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point2.upLeft());

        // Connect headquarter and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, donkeyFarm0.getFlag());

        // Wait for the donkey breeder to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0);

        DonkeyBreeder donkeyBreeder = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof DonkeyBreeder) {
                donkeyBreeder = (DonkeyBreeder) worker;
            }
        }

        assertNotNull(donkeyBreeder);
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the donkey breeder has started walking
        assertFalse(donkeyBreeder.isExactlyAtPoint());

        // Remove the next road
        map.removeRoad(road1);

        // Verify that the donkey breeder continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, flag0.getPosition());

        assertEquals(donkeyBreeder.getPosition(), flag0.getPosition());

        // Verify that the donkey breeder returns to the headquarter when it reaches the flag
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getPosition());
    }

    @Test
    public void testDonkeyBreederContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place donkey farm
        var point2 = new Point(14, 4);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point2.upLeft());

        // Connect headquarter and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, donkeyFarm0.getFlag());

        // Wait for the donkey breeder to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0);

        DonkeyBreeder donkeyBreeder = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof DonkeyBreeder) {
                donkeyBreeder = (DonkeyBreeder) worker;
            }
        }

        assertNotNull(donkeyBreeder);
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the donkey breeder has started walking
        assertFalse(donkeyBreeder.isExactlyAtPoint());

        // Remove the current road
        map.removeRoad(road0);

        // Verify that the donkey breeder continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, flag0.getPosition());

        assertEquals(donkeyBreeder.getPosition(), flag0.getPosition());

        // Verify that the donkey breeder continues to the final flag
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, donkeyFarm0.getFlag().getPosition());

        // Verify that the donkey breeder goes out to the donkey farm instead of going directly back
        assertNotEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testDonkeyBreederReturnsToStorageIfDonkeyFarmIsDestroyed() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place donkey farm
        var point2 = new Point(14, 4);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point2.upLeft());

        // Connect headquarter and first flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        var road1 = map.placeAutoSelectedRoad(player0, flag0, donkeyFarm0.getFlag());

        // Wait for the donkey breeder to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0);

        DonkeyBreeder donkeyBreeder = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof DonkeyBreeder) {
                donkeyBreeder = (DonkeyBreeder) worker;
            }
        }

        assertNotNull(donkeyBreeder);
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        // Wait for the donkey breeder to reach the first flag
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, flag0.getPosition());

        map.stepTime();

        // See that the donkey breeder has started walking
        assertFalse(donkeyBreeder.isExactlyAtPoint());

        // Tear down the donkey farm
        donkeyFarm0.tearDown();

        // Verify that the donkey breeder continues walking to the next flag
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, donkeyFarm0.getFlag().getPosition());

        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getFlag().getPosition());

        // Verify that the donkey breeder goes back to storage
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testDonkeyBreederGoesOffroadBackToClosestStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(17, 17);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Occupy the donkey farm
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Place a second storage closer to the donkey farm
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the donkey farm
        var donkeyBreeder = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarter
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, storehouse0.getPosition());

        // Verify that the donkey breeder is stored correctly in the headquarter
        assertEquals(storehouse0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederReturnsOffroadAndAvoidsBurningStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(17, 17);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Occupy the donkey farm
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Place a second storage closer to the donkey farm
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Destroy the donkey farm
        var donkeyBreeder = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarter
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getPosition());

        // Verify that the donkey breeder is stored correctly in the headquarter
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederReturnsOffroadAndAvoidsDestroyedStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(17, 17);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Occupy the donkey farm
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Place a second storage closer to the donkey farm
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        Utils.constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Wait for the storage to burn down
        Utils.waitForBuildingToBurnDown(storehouse0);

        // Destroy the donkey farm
        var donkeyBreeder = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarter
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getPosition());

        // Verify that the donkey breeder is stored correctly in the headquarter
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testDonkeyBreederReturnsOffroadAndAvoidsUnfinishedStorageWhenDonkeyFarmIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(17, 17);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Occupy the donkey farm
        Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Place a second storage closer to the donkey farm
        var point2 = new Point(13, 13);
        var storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the donkey farm
        var donkeyBreeder = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getPosition());

        donkeyFarm0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarter
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(DONKEY_BREEDER);

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getPosition());

        // Verify that the donkey breeder is stored correctly in the headquarter
        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point25 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place donkey farm
        var point26 = new Point(17, 17);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point26);

        // Place road to connect the headquarter and the donkey farm
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), donkeyFarm0.getFlag());

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Wait for a worker to start walking to the building
        var worker = Utils.waitForWorkersOutsideBuilding(DonkeyBreeder.class, 1, player0).getFirst();

        // Wait for the worker to get to the building's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, donkeyFarm0.getFlag().getPosition());

        // Tear down the building
        donkeyFarm0.tearDown();

        // Verify that the worker goes to the building and then returns to the headquarter instead of entering
        assertEquals(worker.getTarget(), donkeyFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, donkeyFarm0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testDonkeyFarmWithoutResourcesHasZeroProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(7, 9);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm);

        // Populate the donkey farm
        var donkeyBreeder0 = Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm);

        assertTrue(donkeyBreeder0.isInsideBuilding());
        assertEquals(donkeyBreeder0.getHome(), donkeyFarm);
        assertEquals(donkeyFarm.getWorker(), donkeyBreeder0);

        // Verify that the productivity is 0% when the donkey farm doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(donkeyFarm.getFlag().getStackedCargo().isEmpty());
            assertNull(donkeyBreeder0.getCargo());
            assertEquals(donkeyFarm.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyFarmWithAbundantResourcesHasFullProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(7, 9);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm);

        // Populate the donkey farm
        var donkeyBreeder0 = Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm);

        assertTrue(donkeyBreeder0.isInsideBuilding());
        assertEquals(donkeyBreeder0.getHome(), donkeyFarm);
        assertEquals(donkeyFarm.getWorker(), donkeyBreeder0);

        // Connect the donkey farm with the headquarter
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), donkeyFarm.getFlag());

        // Make the donkey farm create some donkeys with full resources available
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (donkeyFarm.needsMaterial(WATER) && donkeyFarm.getAmount(WATER) < 2) {
                donkeyFarm.putCargo(new Cargo(WATER, map));
            }

            if (donkeyFarm.needsMaterial(WHEAT) && donkeyFarm.getAmount(WHEAT) < 2) {
                donkeyFarm.putCargo(new Cargo(WHEAT, map));
            }
        }

        // Verify that the productivity is 100% and stays there
        assertEquals(donkeyFarm.getProductivity(), 100);

        for (int i = 0; i < 1000; i++) {
            map.stepTime();


            if (donkeyFarm.needsMaterial(WATER) && donkeyFarm.getAmount(WATER) < 2) {
                donkeyFarm.putCargo(new Cargo(WATER, map));
            }

            if (donkeyFarm.needsMaterial(WHEAT) && donkeyFarm.getAmount(WHEAT) < 2) {
                donkeyFarm.putCargo(new Cargo(WHEAT, map));
            }

            assertEquals(donkeyFarm.getProductivity(), 100);
        }
    }

    @Test
    public void testDonkeyFarmLosesProductivityWhenResourcesRunOut() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(7, 9);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm);

        // Populate the donkey farm
        var donkeyBreeder0 = Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm);

        assertTrue(donkeyBreeder0.isInsideBuilding());
        assertEquals(donkeyBreeder0.getHome(), donkeyFarm);
        assertEquals(donkeyFarm.getWorker(), donkeyBreeder0);

        // Connect the donkey farm with the headquarter
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), donkeyFarm.getFlag());

        // Make the donkey farm create some donkeys with full resources available
        for (int i = 0; i < 1000; i++) {
            map.stepTime();

            if (donkeyFarm.needsMaterial(WATER) && donkeyFarm.getAmount(WATER) < 2) {
                donkeyFarm.putCargo(new Cargo(WATER, map));
            }

            if (donkeyFarm.needsMaterial(WHEAT) && donkeyFarm.getAmount(WHEAT) < 2) {
                donkeyFarm.putCargo(new Cargo(WHEAT, map));
            }
        }

        // Verify that the productivity goes down when resources run out
        assertEquals(donkeyFarm.getProductivity(), 100);

        for (int i = 0; i < 5000; i++) {
            map.stepTime();
        }

        assertEquals(donkeyFarm.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedDonkeyFarmHasNoProductivity() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(7, 9);
        var donkeyFarm = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm);

        // Verify that the unoccupied donkey farm is unproductive
        for (int i = 0; i < 1000; i++) {
            assertEquals(donkeyFarm.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testDonkeyFarmCanProduce() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(7, 9);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Finish construction of the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Populate the donkey farm
        var donkeyBreeder0 = Utils.occupyBuilding(new DonkeyBreeder(player0, map), donkeyFarm0);

        // Verify that the donkey farm can produce
        assertTrue(donkeyFarm0.canProduce());
    }

    @Test
    public void testDonkeyFarmReportsCorrectOutput() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(6, 12);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Construct the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Verify that the reported output is correct
        assertEquals(donkeyFarm0.getProducedMaterial().length, 1);
        assertEquals(donkeyFarm0.getProducedMaterial()[0], DONKEY);
    }

    @Test
    public void testDonkeyFarmReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(6, 12);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(donkeyFarm0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(donkeyFarm0.getTypesOfMaterialNeeded().contains(PLANK));
        assertTrue(donkeyFarm0.getTypesOfMaterialNeeded().contains(STONE));
        assertEquals(donkeyFarm0.getCanHoldAmount(PLANK), 3);
        assertEquals(donkeyFarm0.getCanHoldAmount(STONE), 3);

        for (var material : Material.values()) {
            if (material == PLANK || material == STONE) {
                continue;
            }

            assertEquals(donkeyFarm0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testDonkeyFarmReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(6, 12);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Construct the donkey farm
        Utils.constructHouse(donkeyFarm0);

        // Verify that the reported needed construction material is correct
        assertEquals(donkeyFarm0.getTypesOfMaterialNeeded().size(), 2);
        assertTrue(donkeyFarm0.getTypesOfMaterialNeeded().contains(WATER));
        assertTrue(donkeyFarm0.getTypesOfMaterialNeeded().contains(WHEAT));
        assertEquals(donkeyFarm0.getCanHoldAmount(WATER), 6);
        assertEquals(donkeyFarm0.getCanHoldAmount(WHEAT), 6);

        for (var material : Material.values()) {
            if (material == WATER || material == WHEAT) {
                continue;
            }

            assertEquals(donkeyFarm0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testWhenDonkeyDeliveryAreBlockedDonkeyFarmProducesNoMoreDonkeys() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(7, 9);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Place road to connect the donkey farm with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        // Wait for the donkey farm to get constructed and occupied
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(donkeyFarm0);

        var donkeyBreeder0 = Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyFarm0);

        assertTrue(donkeyBreeder0.isInsideBuilding());
        assertEquals(donkeyBreeder0.getHome(), donkeyFarm0);
        assertEquals(donkeyFarm0.getWorker(), donkeyBreeder0);

        // Add a lot of material to the headquarter for the donkey farm to consume
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        // Block storage of donkeys
        headquarter0.blockDeliveryOfMaterial(DONKEY);

        // Verify that the donkey farm stops producing donkeys
        for (int i = 0; i < 500; i++) {
            map.stepTime();

            for (var worker : map.getWorkers()) {
                assertFalse(worker instanceof Donkey);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndDonkeyFarmIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place donkey farm
        var point2 = new Point(18, 6);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point2);

        // Place road to connect the storehouse with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarter with the donkey farm
        var road1 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarter
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the donkey farm and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, donkeyFarm0);

        // Add a lot of material to the headquarter for the donkey farm to consume
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        // Wait for the donkey farm and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, donkeyFarm0);

        var donkeyBreeder0 = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder0.isInsideBuilding());
        assertEquals(donkeyBreeder0.getHome(), donkeyFarm0);
        assertEquals(donkeyFarm0.getWorker(), donkeyBreeder0);

        // Verify that the worker goes to the storage when the donkey farm is torn down
        headquarter0.blockDeliveryOfMaterial(DONKEY_BREEDER);

        donkeyFarm0.tearDown();

        map.stepTime();

        assertFalse(donkeyBreeder0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder0, donkeyFarm0.getFlag().getPosition());

        assertEquals(donkeyBreeder0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, donkeyBreeder0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(donkeyBreeder0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndDonkeyFarmIsTornDown() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        var point1 = new Point(5, 5);
        var storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place donkey farm
        var point2 = new Point(18, 6);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point2);

        // Place road to connect the storehouse with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarter with the donkey farm
        var road1 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarter
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the donkey farm and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, donkeyFarm0);

        // Add a lot of material to the headquarter for the donkey farm to consume
        Utils.adjustInventoryTo(headquarter0, WATER, 40);
        Utils.adjustInventoryTo(headquarter0, WHEAT, 40);

        // Wait for the donkey farm and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, donkeyFarm0);

        var donkeyBreeder0 = donkeyFarm0.getWorker();

        assertTrue(donkeyBreeder0.isInsideBuilding());
        assertEquals(donkeyBreeder0.getHome(), donkeyFarm0);
        assertEquals(donkeyFarm0.getWorker(), donkeyBreeder0);

        // Verify that the worker goes to the storage off-road when the donkey farm is torn down
        headquarter0.blockDeliveryOfMaterial(DONKEY_BREEDER);

        donkeyFarm0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(donkeyBreeder0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder0, donkeyFarm0.getFlag().getPosition());

        assertEquals(donkeyBreeder0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(donkeyBreeder0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that worker goes out and in continuously when sent out without being blocked
        Utils.adjustInventoryTo(headquarter0, DONKEY_BREEDER, 1);

        assertEquals(headquarter0.getAmount(DONKEY_BREEDER), 1);

        headquarter0.pushOutAll(DONKEY_BREEDER);

        for (int i = 0; i < 10; i++) {
            var worker = Utils.waitForWorkerOutsideBuilding(DonkeyBreeder.class, player0);

            assertEquals(headquarter0.getAmount(DONKEY_BREEDER), 0);
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
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that worker goes out and in continuously when sent out without being blocked
        Utils.adjustInventoryTo(headquarter0, DONKEY_BREEDER, 1);

        headquarter0.blockDeliveryOfMaterial(DONKEY_BREEDER);
        headquarter0.pushOutAll(DONKEY_BREEDER);

        var worker = Utils.waitForWorkerOutsideBuilding(DonkeyBreeder.class, player0);

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
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(7, 9);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Place road to connect the donkey farm with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the donkey farm to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(donkeyFarm0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(donkeyFarm0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter */
       
        headquarter0.blockDeliveryOfMaterial(DONKEY_BREEDER);

        var worker = donkeyFarm0.getWorker();

        donkeyFarm0.tearDown();

        assertEquals(worker.getPosition(), donkeyFarm0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, donkeyFarm0.getFlag().getPosition());

        assertEquals(worker.getPosition(), donkeyFarm0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), donkeyFarm0.getPosition());
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
        List<Player> players = new ArrayList<>();
        players.add(player0);
        var map = new GameMap(players, 40, 40);

        // Place headquarter
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place donkey farm
        var point1 = new Point(7, 9);
        var donkeyFarm0 = map.placeBuilding(new DonkeyFarm(player0), point1);

        // Place road to connect the donkey farm with the headquarter
        var road0 = map.placeAutoSelectedRoad(player0, donkeyFarm0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the donkey farm to get constructed
        Utils.waitForBuildingToBeConstructed(donkeyFarm0);

        // Wait for a donkey breeder to start walking to the donkey farm
        DonkeyBreeder donkeyBreeder = Utils.waitForWorkerOutsideBuilding(DonkeyBreeder.class, player0);

        // Wait for the donkey breeder to go past the headquarter's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, headquarter0.getFlag().getPosition());

        map.stepTime();

        // Verify that the donkey breeder goes away and dies when the house has been torn down and storage is not possible
        assertEquals(donkeyBreeder.getTarget(), donkeyFarm0.getPosition());

        headquarter0.blockDeliveryOfMaterial(DONKEY_BREEDER);

        donkeyFarm0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, donkeyFarm0.getFlag().getPosition());

        assertEquals(donkeyBreeder.getPosition(), donkeyFarm0.getFlag().getPosition());
        assertNotEquals(donkeyBreeder.getTarget(), headquarter0.getPosition());
        assertFalse(donkeyBreeder.isInsideBuilding());
        assertNull(donkeyFarm0.getWorker());
        assertNotNull(donkeyBreeder.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, donkeyBreeder, donkeyBreeder.getTarget());

        var point = donkeyBreeder.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(donkeyBreeder.isDead());
            assertEquals(donkeyBreeder.getPosition(), point);
            assertTrue(map.getWorkers().contains(donkeyBreeder));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(donkeyBreeder));
    }
}
