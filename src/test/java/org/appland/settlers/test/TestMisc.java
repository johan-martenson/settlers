package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Donkey;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Scout;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Shipyard;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.PlayerColor.BLUE;
import static org.appland.settlers.model.Vegetation.*;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.appland.settlers.model.actors.Soldier.Rank.*;
import static org.junit.Assert.*;

public class TestMisc {

    /*
    TODO:
     - Coin delivery to upgraded building
     - Evacuation of military building is disabled when a player takes over a military building DONE
     - Already destroyed building that is in area lost to enemy does not get torn down again DONE
     - Remove flag with cargo on that is promised for delivery to a building resets the promise
     */


    @Test
    public void testRemoveRoadWhenCourierGoesToBuildingToDeliverCargo() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 500, 250);

        // Place headquarters
        var point0 = new Point(429, 201);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(434, 200);
        var flag0 = map.placeFlag(player0, point1);

        // Place automatic road between flag and headquarter's flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag().getPosition(), point1);

        // Place woodcutter by the flag
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), flag0.getPosition().upLeft());

        // Wait for the road to get an assigned courier
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        // Fast forward a bit until the courier is carrying a cargo to deliver to the woodcutter
        for (int i = 0; i < 2000; i++) {
            if (courier.getCargo() != null &&
                    woodcutter0.getPosition().equals(courier.getTarget()) &&
                    courier.getNextPoint().equals(woodcutter0.getPosition().downRight())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(map.getWorkers().size() >= 2);
        assertNotNull(courier.getCargo());
        assertEquals(courier.getTarget(), woodcutter0.getPosition());
        assertEquals(courier.getLastPoint(), headquarter0.getFlag().getPosition().right());
        assertEquals(courier.getNextPoint(), woodcutter0.getPosition().downRight());

        // Remove the flag and cause the woodcutter to get torn down
        map.removeFlag(map.getFlagAtPoint(point1));

        assertTrue(map.getWorkers().size() >= 2);

        // Verify that the courier goes back to the headquarters
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertEquals(courier.getPosition(), headquarter0.getPosition());
        assertFalse(map.getWorkers().contains(courier));
    }

    @Test
    public void testScoutReturnsWhenFlagRemainsButRoadHasBeenRemoved() throws Exception {

        for (int i = 0; i < 20; i++) {

            // Starting new game
            var player0 = new Player("Player 0", BLUE, Nation.ROMANS, PlayerType.HUMAN);
            var map = new GameMap(List.of(player0), 500, 250);

            // Place headquarters
            var point0 = new Point(429, 201);
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            // Place flag
            var point1 = new Point(434, 200);
            var flag0 = map.placeFlag(player0, point1);

            // Call scout
            flag0.callScout();

            // Create a road that connects the flag with the headquarters' flag
            var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            // Wait for a scout to appear
            var scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player0).getFirst();

            // Wait the scout to get to the flag
            assertEquals(scout.getTarget(), flag0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag0.getPosition());

            assertEquals(scout.getPosition(), flag0.getPosition());

            // Wait for the scout to continue away from the flag
            Utils.fastForward(10, map);

            assertNotEquals(scout.getPosition(), flag0.getPosition());

            // Remove the road so the scout has no way back using roads
            map.removeRoad(road0);

            // Wait for the scout to get back to the flag
            for (int j = 0; j < 20000; j++) {
                if (scout.getPosition().equals(flag0.getPosition()) &&
                        Objects.equals(scout.getTarget(), headquarter0.getPosition())) {
                    break;
                }

                map.stepTime();
            }

            assertEquals(scout.getTarget(), headquarter0.getPosition());
            assertEquals(scout.getPosition(), flag0.getPosition());

            // Verify that the scout goes back to the headquarters
            Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter0.getPosition());

            assertEquals(scout.getPosition(), headquarter0.getPosition());
        }
    }

    @Test
    public void testPlaceFirstBuildingOnEdgeOfScreen() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 500, 250);

        // Verify that it's not possible to place a headquarters on the edge of the screen
        var point0 = new Point(0, 2);

        try {
            var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testGetPossibleAdjacentRoadConnectionsIncludingEndpointsOutsideMap() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Verify that trying to get possible road connections from a point outside the map throws invalid user action exception
        try {
            map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player0, new Point(174, 132));

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testPlaceRoadWithoutPoints() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 500, 250);

        // Place headquarters
        var point0 = new Point(429, 201);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that placing a road without any points throws an invalid user action exception
        try {
            map.placeRoad(player0, new ArrayList<>());

            fail();
        } catch (InvalidUserActionException e) { }
    }

    @Test
    public void testUnoccupiedMilitaryBuildingDoesNotIncreaseDiscoveredArea() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point01 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point01);

        // Place guard houses
        var point02 = new Point(5, 13);
        var guardHouse0 = map.placeBuilding(new GuardHouse(player0), point02);

        var point03 = new Point(19, 5);
        var guardHouse1 = map.placeBuilding(new GuardHouse(player0), point03);

        // Finish construction of both guard houses
        Utils.constructHouse(guardHouse0);
        Utils.constructHouse(guardHouse1);

        // Connect the first guard house to the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        // Verify that the discovered area is only extended around the guard house that gets occupied
        var point04 = new Point(5, 23);
        var point05 = new Point(33, 5);

        assertFalse(player0.getDiscoveredLand().contains(point04));
        assertFalse(player0.getDiscoveredLand().contains(point05));

        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertNotNull(military);

        // Verify that the discovered area is only extended around the first guardhouse and not the second
        assertEquals(military.getTarget(), guardHouse0.getPosition());
        assertFalse(player0.getDiscoveredLand().contains(point04));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, guardHouse0.getPosition());

        assertTrue(player0.getDiscoveredLand().contains(point04));
        assertFalse(player0.getDiscoveredLand().contains(point05));
    }

    @Test
    public void testBuildingWhereConstructionHasNotStartedIsAtZeroPercentProgress() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Verify that the construction is at zero progress
        assertEquals(armory0.getConstructionProgress(), 0);
    }

    @Test
    public void testConstructionProgressNeverGoesBackwards() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Verify that the construction progress never goes backwards
        int previousProgress = armory0.getConstructionProgress();
        for (int i = 0; i < 1000; i++) {
            assertTrue(armory0.getConstructionProgress() >= previousProgress);

            previousProgress = armory0.getConstructionProgress();

            if (!armory0.isUnderConstruction()) {
                break;
            }

            map.stepTime();
        }
    }

    @Test
    public void testFullyConstructedBuildingIsAtHundredPercentProgress() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place armory
        var point1 = new Point(6, 12);
        var armory0 = map.placeBuilding(new Armory(player0), point1);

        // Construct the building
        Utils.constructHouse(armory0);

        // Verify that the construction is at hundred progress
        assertEquals(armory0.getConstructionProgress(), 100);
    }

    @Test
    public void testNoMonitoringEventWithEmptyPathForStorageWorker() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        // Set up monitoring subscription for the player
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        // Place a woodcutter
        var point1 = new Point(10, 10);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter to the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        // Wait for the woodcutter to get constructed
        GameChangesList lastEvent = null;
        for (int i = 0; i < 1000; i++) {
            if (woodcutter0.isReady()) {
                break;
            }

            if (monitor.getLastEvent() != null) {
                var thisEvent = monitor.getLastEvent();

                if (!thisEvent.equals(lastEvent)) {
                    for (var worker : monitor.getLastEvent().workersWithNewTargets()) {
                        assertNotNull(worker.getPlannedPath());
                        assertNotEquals(worker.getPlannedPath().size(), 0);
                    }
                }
            }

            map.stepTime();

            lastEvent = monitor.getLastEvent();
        }

        assertTrue(woodcutter0.isReady());
    }

    @Test
    public void testMonitoringEventWhenWorkerLeavesBuilding() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(5, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        // Place a woodcutter
        var point1 = new Point(10, 10);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter to the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        // Wait for the woodcutter to get constructed and populated
        Utils.waitForBuildingToBeConstructed(woodcutter0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        var woodcutterWorker = woodcutter0.getWorker();

        map.stepTime();

        // Place a tree that the woodcutter can cut down
        var point2 = new Point(14, 12);
        var tree0 = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Set up monitoring subscription for the player
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        // Verify that an event is sent when the worker goes out to cut down the tree
        assertTrue(woodcutterWorker.isInsideBuilding());

        int amountEvents = monitor.getEvents().size();

        Utils.waitForWorkerToBeOutside(woodcutterWorker, map);

        assertEquals(woodcutterWorker.getTarget(), tree0.getPosition());
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertTrue(monitor.getEvents().size() > amountEvents);

        var gameChangesList = monitor.getLastEvent();

        assertTrue(gameChangesList.workersWithNewTargets().contains(woodcutterWorker));
    }

    @Test
    public void testUnoccupiedMilitaryBuildingIsDestroyedWhenEnemyBarracksIsOccupied() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(45, 5);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks for player 0
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1
        var point3 = new Point(29, 5);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Verify that player 1's barracks is torn down when player 0's barracks gets occupied
        assertTrue(barracks1.isReady());
        assertFalse(barracks1.isOccupied());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        assertTrue(barracks1.isBurningDown());
        assertFalse(map.isFlagAtPoint(barracks1.getFlag().getPosition()));
    }

    @Test
    public void testEvacuationIsDisabledWhenPlayerTakesOverBuilding() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks for player 0
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1
        var point3 = new Point(21, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        // Empty barracks 1
        var road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        barracks1.evacuate();

        var military = Utils.waitForSoldierOutsideBuilding(player1);

        assertEquals(military.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter1.getPosition());

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);

        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Order an attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);

        // Wait for the attacker to get to the attacked buildings flag
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        map.stepTime();

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        // Verify that evacuation is disabled when the attacker takes over the building
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);
        assertEquals(barracks1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(barracks1.isReady());
        assertTrue(barracks1.isEvacuated());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertFalse(barracks1.isEvacuated());
        assertEquals(barracks1.getPlayer(), player0);
        assertTrue(barracks1.getHostedSoldiers().contains(attacker));
        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
    }

    @Test
    public void testAllRanksCanReturnToStorage() throws Exception {

        // Starting new game
        var player0 = new Player("Player 0", BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(15, 15);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place fortress
        var point1 = new Point(20, 16);
        var fortress0 = map.placeBuilding(new Fortress(player0), point1);

        // Connect the fortress with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, fortress0.getFlag(), headquarter0.getFlag());

        // Ensure that there is one soldier of each rank in the headquarters
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 1);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 1);

        // Wait for the fortress to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(fortress0);
        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 5);

        // Verify that when the fortress is burned down, all soldiers go back and get stored properly
        assertEquals(headquarter0.getAmount(PRIVATE), 0);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 0);
        assertEquals(headquarter0.getAmount(SERGEANT), 0);
        assertEquals(headquarter0.getAmount(OFFICER), 0);
        assertEquals(headquarter0.getAmount(GENERAL), 0);

        fortress0.tearDown();

        var soldiers = Utils.waitForWorkersOutsideBuilding(Soldier.class, 5, player0);

        boolean stillOnMap;
        for (int i = 0; i < 5000; i++) {
            stillOnMap = false;

            for (var soldier : soldiers) {
                if (map.getWorkers().contains(soldier)) {
                    stillOnMap = true;

                    break;
                }
            }

            if (!stillOnMap) {
                break;
            }

            map.stepTime();
        }

        for (var soldier : soldiers) {
            assertFalse(map.getWorkers().contains(soldier));
        }

        assertEquals(headquarter0.getAmount(PRIVATE), 1);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 1);
        assertEquals(headquarter0.getAmount(SERGEANT), 1);
        assertEquals(headquarter0.getAmount(OFFICER), 1);
        assertEquals(headquarter0.getAmount(GENERAL), 1);

    }

    @Test
    public void testUnoccupiedMilitaryBuildingIsTornDownWhenAreaIsLost() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks for player 0
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1
        var point3 = new Point(21, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        // Place a second barracks for player 1
        var point4 = new Point(13, 15);
        var barracks2 = map.placeBuilding(new Barracks(player1), point4);

        // Construct the new barracks
        Utils.constructHouse(barracks2);

        // Remove all soldiers from the headquarters
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        // Place a road that connects the two barracks for player 1
        var road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), barracks2.getFlag());

        // Empty barracks 1
        var road1 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        barracks1.evacuate();

        var military = Utils.waitForSoldierOutsideBuilding(player1);

        assertEquals(military.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter1.getPosition());

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);

        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Order an attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);

        // Wait for the attacker to get to the attacked buildings flag
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        map.stepTime();

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        // Verify that the second barracks is destroyed, its road, and the road between the barracks are removed
        var road2 = map.getRoad(barracks2.getPosition(), barracks2.getFlag().getPosition());

        assertTrue(map.getRoads().contains(road0));
        assertTrue(map.getRoads().contains(road2));
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);
        assertEquals(barracks1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(barracks1.isReady());
        assertTrue(barracks1.isEvacuated());
        assertTrue(barracks2.isUnoccupied());
        assertEquals(barracks2.getPlayer(), player1);

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);
        assertFalse(map.getRoads().contains(road0));
        assertFalse(map.getRoads().contains(road2));
        assertTrue(player0.getOwnedLand().contains(barracks2.getPosition()));
        assertTrue(player0.getOwnedLand().contains(barracks2.getFlag().getPosition()));
        assertFalse(player1.isWithinBorder(barracks2.getPosition()));
        assertTrue(barracks2.isBurningDown());
    }

    @Test
    public void testCourierDeliveringToBuildingMakesCargoDisappearIfTargetUnderConstructionBuildingIsTornDownAndReturnToStorageIsNotPossible() throws InvalidUserActionException {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(15, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Assign builder
        Utils.assignBuilder(woodcutter);

        // Connect the woodcutter with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        // Wait for a courier to come to the road
        var courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        // Wait for the courier to carry a cargo to the woodcutter
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getTarget(), woodcutter);

        // Verify that the courier goes to the woodcutter and the cargo disappears if the woodcutter is torn down and it cannot be returned
        headquarter0.blockDeliveryOfMaterial(courier.getCargo().getMaterial());

        assertTrue(woodcutter.isUnderConstruction());

        woodcutter.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, woodcutter.getFlag().getPosition());

        assertEquals(woodcutter.getAmount(PLANK), 0);
        assertEquals(woodcutter.getAmount(STONE), 0);
        assertEquals(courier.getPosition(), woodcutter.getFlag().getPosition());

        assertNull(courier.getCargo());
        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 0);
    }

    @Test
    public void testCourierDeliveringToBuildingMakesCargoDisappearIfPlannedTargetBuildingIsTornDownAndReturnToStorageIsNotPossible() throws InvalidUserActionException {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(15, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        // Wait for a courier to come to the road
        var courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        // Wait for the courier to carry a cargo to the woodcutter
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getTarget(), woodcutter);

        // Verify that the courier goes to the woodcutter and the cargo disappears if the woodcutter is torn down and it cannot be returned
        headquarter0.blockDeliveryOfMaterial(courier.getCargo().getMaterial());

        assertTrue(woodcutter.isPlanned());

        woodcutter.tearDown();

        assertFalse(map.isBuildingAtPoint(point1));
        assertNull(map.getBuildingAtPoint(point1));

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, woodcutter.getFlag().getPosition());

        assertEquals(woodcutter.getAmount(PLANK), 0);
        assertEquals(woodcutter.getAmount(STONE), 0);
        assertEquals(courier.getPosition(), woodcutter.getFlag().getPosition());

        assertNull(courier.getCargo());
        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 0);
    }

    @Test
    public void testCourierDeliveringToFlagMakesCargoDisappearIfTargetBuildingIsTornDownAndReturnToStorageIsNotPossible() throws InvalidUserActionException {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(17, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Place flag
        var point2 = new Point(14, 4);
        var flag0 = map.placeFlag(player0, point2);

        // Connect the headquarters with the flag
        var road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the flag with the woodcutter
        var road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter.getFlag());

        // Wait for the first road to get assigned a courier
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        // Wait for the courier to carry a cargo intended for the woodcutter
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());

        // Verify that the courier goes to the flag and the cargo disappears if the woodcutter is torn down and it cannot be returned
        var cargo = courier.getCargo();

        headquarter0.blockDeliveryOfMaterial(courier.getCargo().getMaterial());

        woodcutter.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        assertEquals(woodcutter.getAmount(PLANK), 0);
        assertEquals(woodcutter.getAmount(STONE), 0);
        assertEquals(courier.getPosition(), flag0.getPosition());
        assertNull(courier.getCargo());
        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 0);
        assertFalse(flag0.getStackedCargo().contains(cargo));
        assertEquals(courier.getTarget(), flag0.getPosition().left());
    }

    @Ignore // Problem: the torn down building is removed before the fight finishes
    @Test
    public void testAttackerCapturesAreaWithAlreadyDestroyedBuilding() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place player 0's headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        var point1 = new Point(37, 15);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place barracks for player 0
        var point2 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point2);

        // Place barracks for player 1
        var point3 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point3);

        // Place woodcutter close to barracks 1
        var point4 = new Point(26, 14);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player1), point4);

        // Finish construction
        Utils.constructHouses(barracks0, barracks1, woodcutter0);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Go forward a bit to time it so that the woodcutter is still on the map when the soldier takes over the building
        Utils.fastForward(30, map);

        // Find the military that was chosen to attack
        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        // Verify that a military leaves the attacked building to defend when the attacker reaches the flag
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Wait for the defender to go to the attacker
        var defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Tear down the woodcutter
        woodcutter0.tearDown();

        // Wait for the general to beat the private
        Utils.waitForSoldierToBeDying(defender, map);

        // Verify that player 1's barracks is in player 1's border and not player 0's
        Utils.verifyPointIsNotWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsWithinBorder(player1, barracks1.getPosition());

        assertTrue(map.getBuildings().contains(woodcutter0));

        // Wait for the attacker to return to the fixed point
        Utils.waitForWorkerToHaveTarget(map, attacker, barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        // Wait for the attacker to go to the barracks
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(woodcutter0.isDestroyed());
        assertTrue(map.getBuildings().contains(woodcutter0));

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        // Verify that the border is updated to include the captured building and that it's not in player 1's border anymore
        assertTrue(map.getBuildings().contains(woodcutter0));
        assertTrue(woodcutter0.isDestroyed());
        assertTrue(player0.getOwnedLand().contains(woodcutter0.getPosition()));

        Utils.verifyPointIsWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsNotWithinBorder(player1, barracks1.getPosition());
        assertEquals(player0.getPlayerAtPoint(barracks1.getPosition()), player0);
    }

    @Test
    public void testRemovingFlagWithCargoOnItResetsPromisedDelivery() throws InvalidUserActionException {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(14, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place woodcutter
        var point2 = new Point(17, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        // Place road to connect the headquarters with the flag
        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        // Place road to connect the flag with the woodcutter
        var road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter.getFlag());

        // Wait for courier to get assigned to the first road
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        // Wait for the courier to hold a cargo for the woodcutter
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getTarget(), woodcutter);

        var cargo = courier.getCargo();

        // Wait for the courier to place the cargo on the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        assertNull(courier.getCargo());
        assertTrue(flag0.getStackedCargo().contains(cargo));

        // Remove the flag so the cargo disappears and the promise to deliver it is broken
        map.removeFlag(flag0);

        // Place a new road to connect the headquarters directly with the woodcutter
        var road2 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        // Verify that the woodcutter gets all deliveries it needs and gets constructed
        Utils.waitForBuildingToBeConstructed(woodcutter);

        assertTrue(woodcutter.isReady());
    }

    @Test
    public void testRemovingRoadWithCourierCarryingCargoResetsPromisedDelivery() throws InvalidUserActionException {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place flag
        var point1 = new Point(14, 4);
        var flag0 = map.placeFlag(player0, point1);

        // Place woodcutter
        var point2 = new Point(17, 5);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        // Place road to connect the headquarters with the flag
        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        // Place road to connect the flag with the woodcutter
        var road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter.getFlag());

        // Wait for courier to get assigned to the first road
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        // Wait for the courier to hold a cargo for the woodcutter
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getTarget(), woodcutter);

        var cargo = courier.getCargo();

        // Wait for the courier to reach the middle of the road
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition().left());

        assertNotNull(courier.getCargo());

        // Remove the flag so the cargo disappears and the promise to deliver it is broken
        map.removeFlag(flag0);

        // Place a new road to connect the headquarters directly with the woodcutter
        var road2 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        // Verify that the woodcutter gets all deliveries it needs and gets constructed
        Utils.waitForBuildingToBeConstructed(woodcutter);

        assertTrue(woodcutter.isReady());
    }

    @Test
    public void testCannotPlaceHeadquarterTooCloseToRightEdgeOfMap() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 10, 10);

        // Verify that it's not possible to place a headquarters so that it's flag will be on the border
        var point0 = new Point(7, 7);
        try {
            map.placeBuilding(new Headquarter(player0), point0);
            fail();
        } catch (InvalidUserActionException e) { }

        assertEquals(map.getBuildings().size(), 0);
    }

    @Test
    public void testCannotPlaceHeadquarterTooCloseToBottomEdgeOfMap() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 10, 10);

        // Verify that it's not possible to place a headquarters so that it's flag will be on the border
        var point0 = new Point(7, 3);
        try {
            map.placeBuilding(new Headquarter(player0), point0);

            fail();
        } catch (InvalidUserActionException e) {}

        assertEquals(map.getBuildings().size(), 0);
    }

    @Test
    public void testCannotRemoveFlagThatIsNull() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 10, 10);

        // Verify that it's not possible to remove a flag that is null
        try {
            map.removeFlag(null);

            fail();
        } catch (InvalidUserActionException e) {}

        assertEquals(map.getBuildings().size(), 0);
    }

    @Test
    public void testCannotRemoveRoadFromDriveway() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(9, 9);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        var point1 = new Point(13, 9);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        var road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter.getFlag());

        // Wait for the woodcutter to get constructed
        Utils.waitForBuildingToBeConstructed(woodcutter);

        // Verify that it's not possible to remove a driveway from a house
        var road1 = map.getRoad(woodcutter.getPosition(), woodcutter.getFlag().getPosition());

        try {
            map.removeRoad(road1);

            fail();
        } catch (InvalidUserActionException e) {}

        assertTrue(map.getRoads().contains(road1));
    }

    @Test
    public void testPushOutDonkey() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Push out donkeys
        headquarter.pushOutAll(DONKEY);

        // Verify that donkeys can be pushed out
        Donkey donkey = Utils.waitForWorkerOutsideBuilding(Donkey.class, player0);

        assertNotNull(donkey);
    }

    @Test
    public void testPushOutCourier() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Push out courier
        headquarter.pushOutAll(COURIER);

        // Verify that couriers can be pushed out
        var courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        assertNotNull(courier);
    }

    @Test
    public void testPushOutPrivate() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Push out private
        headquarter.pushOutAll(PRIVATE);

        // Verify that couriers can be pushed out
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(military.getRank(), PRIVATE_RANK);
    }

    @Test
    public void testPushOutPrivateFirstClass() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Put a private first class into the headquarters
        Utils.adjustInventoryTo(headquarter, PRIVATE_FIRST_CLASS, 1);

        // Push out private first class
        headquarter.pushOutAll(PRIVATE_FIRST_CLASS);

        // Verify that private first class can be pushed out
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(military.getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testPushOutSergeant() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Put a sergeant into the headquarters
        Utils.adjustInventoryTo(headquarter, SERGEANT, 1);

        // Push out sergeant
        headquarter.pushOutAll(SERGEANT);

        // Verify that sergeant can be pushed out
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(military.getRank(), SERGEANT_RANK);
    }

    @Test
    public void testPushOutOfficerWithNoOtherStorageAvailable() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Put an officer into the headquarters
        Utils.adjustInventoryTo(headquarter, OFFICER, 1);

        // Push out officer
        headquarter.pushOutAll(OFFICER);

        // Verify that officer can be pushed out
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(military.getRank(), OFFICER_RANK);

        // Verify that the officer goes to the flag of the headquarters and then back again
    }

    @Test
    public void testPushOutGeneral() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Put a general into the headquarters
        Utils.adjustInventoryTo(headquarter, GENERAL, 1);

        // Push out general
        headquarter.pushOutAll(GENERAL);

        // Verify that general can be pushed out
        var military = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(military.getRank(), GENERAL_RANK);
    }

    @Test
    public void testPlaceHeadquarterInAfricaMap() throws InvalidUserActionException {
        /*   - Up left: STEPPE
             - Above: STEPPE
             - Up right: STEPPE
             - Down right: STEPPE
             - Below: STEPPE
             - Down left: STEPPE

             + Up-left up-right: STEPPE
             + Up-right above: DESERT_1
             + Up-right up-right: DESERT_1
             + Right above: DESERT_1
             + Right up-right: DESERT_1
             + Right down-right: DESERT_1
             + Right below: DESERT_1
             + Down-right down-right: DESERT_1
             + Down-right below: STEPPE
             + Down-right down-left: STEPPE
             + Down-left below: STEPPE
             + Down-left down-left: STEPPE
             + Down-left up-left: STEPPE
             + Left down-left: STEPPE
             + Left up-left: STEPPE
             + Left above: STEPPE
             + Up-left up-left: STEPPE
             + Up-left above: STEPPE
*/
        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Create the terrain used in the africa map
        var point0 = new Point(68, 68);

        map.setVegetationUpLeft(point0, STEPPE);
        map.setVegetationAbove(point0, STEPPE);
        map.setVegetationUpRight(point0, STEPPE);
        map.setVegetationDownRight(point0, STEPPE);
        map.setVegetationBelow(point0, STEPPE);
        map.setVegetationDownLeft(point0, STEPPE);

        map.setVegetationUpRight(point0.upLeft(), STEPPE);
        map.setVegetationAbove(point0.upRight(), DESERT_1);
        map.setVegetationUpRight(point0.upRight(), DESERT_1);
        map.setVegetationAbove(point0.right(), DESERT_1);
        map.setVegetationUpRight(point0.right(), DESERT_1);
        map.setVegetationDownRight(point0.right(), DESERT_1);
        map.setVegetationBelow(point0.right(), DESERT_1);
        map.setVegetationDownRight(point0.downRight(), DESERT_1);
        map.setVegetationBelow(point0.downRight(), STEPPE);
        map.setVegetationDownLeft(point0.downRight(), STEPPE);
        map.setVegetationBelow(point0.downLeft(), STEPPE);
        map.setVegetationDownLeft(point0.downLeft(), STEPPE);
        map.setVegetationUpLeft(point0.downLeft(), STEPPE);
        map.setVegetationDownLeft(point0.left(), STEPPE);
        map.setVegetationUpLeft(point0.left(), STEPPE);
        map.setVegetationAbove(point0.left(), STEPPE);
        map.setVegetationUpLeft(point0.upLeft(), STEPPE);
        map.setVegetationAbove(point0.upLeft(), STEPPE);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        assertTrue(map.isBuildingAtPoint(point0));
        assertEquals(headquarter, map.getBuildingAtPoint(point0));
    }

    @Test
    public void testPlaceHeadquarterInEurope1Map() throws InvalidUserActionException {
        /*   - Up left: SAVANNAH
             - Above: SAVANNAH
             - Up right: STEPPE
             - Down right: STEPPE
             - Below: STEPPE
             - Down left: STEPPE
             + Up-left up-right: SAVANNAH
             + Up-right above: SAVANNAH
             + Up-right up-right: MEADOW_2
             + Right above: MEADOW_1
             + Right up-right: STEPPE
             + Right down-right: STEPPE
             + Right below: MEADOW_1
             + Down-right down-right: DESERT_1
             + Down-right below: DESERT_1
             + Down-right down-left: STEPPE
             + Down-left below: STEPPE
             + Down-left down-left: DESERT_1
             + Down-left up-left: DESERT_1
             + Left down-left: DESERT_1
             + Left up-left: SAVANNAH
             + Left above: SAVANNAH
             + Up-left up-left: SAVANNAH
             + Up-left above: SAVANNAH
             */


        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Create the terrain used in the africa map
        var point0 = new Point(68, 68);

        map.setVegetationUpLeft(point0, SAVANNAH);
        map.setVegetationAbove(point0, SAVANNAH);
        map.setVegetationUpRight(point0, STEPPE);
        map.setVegetationDownRight(point0, STEPPE);
        map.setVegetationBelow(point0, STEPPE);
        map.setVegetationDownLeft(point0, STEPPE);

        map.setVegetationUpRight(point0.upLeft(), SAVANNAH);
        map.setVegetationAbove(point0.upRight(), SAVANNAH);
        map.setVegetationUpRight(point0.upRight(), MEADOW_2);
        map.setVegetationAbove(point0.right(), MEADOW_1);
        map.setVegetationUpRight(point0.right(), STEPPE);
        map.setVegetationDownRight(point0.right(), STEPPE);
        map.setVegetationBelow(point0.right(), MEADOW_1);
        map.setVegetationDownRight(point0.downRight(), DESERT_1);
        map.setVegetationBelow(point0.downRight(), DESERT_1);
        map.setVegetationDownLeft(point0.downRight(), STEPPE);
        map.setVegetationBelow(point0.downLeft(), STEPPE);
        map.setVegetationDownLeft(point0.downLeft(), DESERT_1);
        map.setVegetationUpLeft(point0.downLeft(), DESERT_1);
        map.setVegetationDownLeft(point0.left(), DESERT_1);
        map.setVegetationUpLeft(point0.left(), SAVANNAH);
        map.setVegetationAbove(point0.left(), SAVANNAH);
        map.setVegetationUpLeft(point0.upLeft(), SAVANNAH);
        map.setVegetationAbove(point0.upLeft(), SAVANNAH);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        assertTrue(map.isBuildingAtPoint(point0));
        assertEquals(headquarter, map.getBuildingAtPoint(point0));
    }

    @Test
    public void testPlaceHeadquarterInEurope2Map() throws InvalidUserActionException {
        /*   - Up left: SAVANNAH
             - Above: SAVANNAH
             - Up right: SAVANNAH
             - Down right: SAVANNAH
             - Below: SAVANNAH
             - Down left: SAVANNAH
             + Up-left up-right: SAVANNAH
             + Up-right above: SAVANNAH
             + Up-right up-right: DESERT_1
             + Right above: DESERT_1
             + Right up-right: WATER
             + Right down-right: WATER
             + Right below: DESERT_1
             + Down-right down-right: DESERT_1
             + Down-right below: DESERT_1
             + Down-right down-left: DESERT_1
             + Down-left below: DESERT_1
             + Down-left down-left: SAVANNAH
             + Down-left up-left: SAVANNAH
             + Left down-left: MEADOW_3
             + Left up-left: MEADOW_3
             + Left above: MEADOW_3
             + Up-left up-left: MEADOW_3
             + Up-left above: MEADOW_2
           */

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Create the terrain used in the africa map
        var point0 = new Point(68, 68);

        map.setVegetationUpLeft(point0, SAVANNAH);
        map.setVegetationAbove(point0, SAVANNAH);
        map.setVegetationUpRight(point0, SAVANNAH);
        map.setVegetationDownRight(point0, SAVANNAH);
        map.setVegetationBelow(point0, SAVANNAH);
        map.setVegetationDownLeft(point0, SAVANNAH);

        map.setVegetationUpRight(point0.upLeft(), SAVANNAH);
        map.setVegetationAbove(point0.upRight(), SAVANNAH);
        map.setVegetationUpRight(point0.upRight(), DESERT_1);
        map.setVegetationAbove(point0.right(), DESERT_1);
        map.setVegetationUpRight(point0.right(), WATER);
        map.setVegetationDownRight(point0.right(), WATER);
        map.setVegetationBelow(point0.right(), DESERT_1);
        map.setVegetationDownRight(point0.downRight(), DESERT_1);
        map.setVegetationBelow(point0.downRight(), DESERT_1);
        map.setVegetationDownLeft(point0.downRight(), DESERT_1);
        map.setVegetationBelow(point0.downLeft(), DESERT_1);
        map.setVegetationDownLeft(point0.downLeft(), SAVANNAH);
        map.setVegetationUpLeft(point0.downLeft(), SAVANNAH);
        map.setVegetationDownLeft(point0.left(), MEADOW_3);
        map.setVegetationUpLeft(point0.left(), MEADOW_3);
        map.setVegetationAbove(point0.left(), MEADOW_3);
        map.setVegetationUpLeft(point0.upLeft(), MEADOW_3);
        map.setVegetationAbove(point0.upLeft(), MEADOW_2);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        assertTrue(map.isBuildingAtPoint(point0));
        assertEquals(headquarter, map.getBuildingAtPoint(point0));
    }

    @Test
    public void testPlaceHeadquarterInNorthAsiaMap() throws InvalidUserActionException {
        /*   - Up left: MEADOW_1
             - Above: MEADOW_1
             - Up right: MEADOW_1
             - Down right: MEADOW_1
             - Below: MEADOW_1
             - Down left: MEADOW_1
             + Up-left up-right: MEADOW_1
             + Up-right above: MEADOW_1
             + Up-right up-right: MEADOW_1
             + Right above: MEADOW_1
             + Right up-right: MEADOW_1
             + Right down-right: MEADOW_1
             + Right below: MEADOW_1
             + Down-right down-right: MEADOW_1
             + Down-right below: MEADOW_1
             + Down-right down-left: MEADOW_1
             + Down-left below: DESERT_1
             + Down-left down-left: DESERT_1
             + Down-left up-left: DESERT_1
             + Left down-left: DESERT_1
             + Left up-left: DESERT_1
             + Left above: DESERT_1
             + Up-left up-left: DESERT_1
             + Up-left above: MEADOW_1
           */

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Create the terrain used in the africa map
        var point0 = new Point(68, 68);

        map.setVegetationUpLeft(point0, MEADOW_1);
        map.setVegetationAbove(point0, MEADOW_1);
        map.setVegetationUpRight(point0, MEADOW_1);
        map.setVegetationDownRight(point0, MEADOW_1);
        map.setVegetationBelow(point0, MEADOW_1);
        map.setVegetationDownLeft(point0, MEADOW_1);

        map.setVegetationUpRight(point0.upLeft(), MEADOW_1);
        map.setVegetationAbove(point0.upRight(), MEADOW_1);
        map.setVegetationUpRight(point0.upRight(), MEADOW_1);
        map.setVegetationAbove(point0.right(), MEADOW_1);
        map.setVegetationUpRight(point0.right(), MEADOW_1);
        map.setVegetationDownRight(point0.right(), MEADOW_1);
        map.setVegetationBelow(point0.right(), MEADOW_1);
        map.setVegetationDownRight(point0.downRight(), MEADOW_1);
        map.setVegetationBelow(point0.downRight(), MEADOW_1);
        map.setVegetationDownLeft(point0.downRight(), MEADOW_1);
        map.setVegetationBelow(point0.downLeft(), DESERT_1);
        map.setVegetationDownLeft(point0.downLeft(), DESERT_1);
        map.setVegetationUpLeft(point0.downLeft(), DESERT_1);
        map.setVegetationDownLeft(point0.left(), DESERT_1);
        map.setVegetationUpLeft(point0.left(), DESERT_1);
        map.setVegetationAbove(point0.left(), DESERT_1);
        map.setVegetationUpLeft(point0.upLeft(), DESERT_1);
        map.setVegetationAbove(point0.upLeft(), MEADOW_1);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        assertTrue(map.isBuildingAtPoint(point0));
        assertEquals(headquarter, map.getBuildingAtPoint(point0));
    }

    @Test
    public void testPlaceHeadquarterInOmap06Map() throws InvalidUserActionException {

    /*
         - Up left: MEADOW_2
         - Above: MEADOW_2
         - Up right: MEADOW_2
         - Down right: MEADOW_1
         - Below: MEADOW_1
         - Down left: MEADOW_2
         + Up-left up-right: SAVANNAH
         + Up-right above: WATER
         + Up-right up-right: WATER
         + Right above: MEADOW_2
         + Right up-right: SAVANNAH
         + Right down-right: MEADOW_1
         + Right below: MEADOW_1
         + Down-right down-right: MEADOW_1
         + Down-right below: MEADOW_1
         + Down-right down-left: FLOWER_MEADOW
         + Down-left below: FLOWER_MEADOW
         + Down-left down-left: MEADOW_2
         + Down-left up-left: MEADOW_2
         + Left down-left: MEADOW_2
         + Left up-left: MEADOW_2
         + Left above: MEADOW_2
         + Up-left up-left: SAVANNAH
         + Up-left above: SAVANNAH
       */

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Create the terrain used in the africa map
        var point0 = new Point(68, 68);

        map.setVegetationUpLeft(point0, MEADOW_2);
        map.setVegetationAbove(point0, MEADOW_2);
        map.setVegetationUpRight(point0, MEADOW_2);
        map.setVegetationDownRight(point0, MEADOW_1);
        map.setVegetationBelow(point0, MEADOW_1);
        map.setVegetationDownLeft(point0, MEADOW_2);

        map.setVegetationUpRight(point0.upLeft(), SAVANNAH);
        map.setVegetationAbove(point0.upRight(), WATER);
        map.setVegetationUpRight(point0.upRight(), WATER);
        map.setVegetationAbove(point0.right(), MEADOW_2);
        map.setVegetationUpRight(point0.right(), SAVANNAH);
        map.setVegetationDownRight(point0.right(), MEADOW_1);
        map.setVegetationBelow(point0.right(), MEADOW_1);
        map.setVegetationDownRight(point0.downRight(), MEADOW_1);
        map.setVegetationBelow(point0.downRight(), MEADOW_1);
        map.setVegetationDownLeft(point0.downRight(), FLOWER_MEADOW);
        map.setVegetationBelow(point0.downLeft(), FLOWER_MEADOW);
        map.setVegetationDownLeft(point0.downLeft(), MEADOW_2);
        map.setVegetationUpLeft(point0.downLeft(), MEADOW_2);
        map.setVegetationDownLeft(point0.left(), MEADOW_2);
        map.setVegetationUpLeft(point0.left(), MEADOW_2);
        map.setVegetationAbove(point0.left(), MEADOW_2);
        map.setVegetationUpLeft(point0.upLeft(), SAVANNAH);
        map.setVegetationAbove(point0.upLeft(), SAVANNAH);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        assertTrue(map.isBuildingAtPoint(point0));
        assertEquals(headquarter, map.getBuildingAtPoint(point0));
    }

    @Test
    public void testPlaceHeadquarterInWelt21Map() throws InvalidUserActionException {

    /*
         - Up left: MEADOW_2
         - Above: MEADOW_2
         - Up right: MEADOW_1
         - Down right: MEADOW_3
         - Below: MEADOW_3
         - Down left: MEADOW_2
         + Up-left up-right: MEADOW_3
         + Up-right above: FLOWER_MEADOW
         + Up-right up-right: FLOWER_MEADOW
         + Right above: MEADOW_1
         + Right up-right: MEADOW_1
         + Right down-right: MEADOW_1
         + Right below: MEADOW_3
         + Down-right down-right: SWAMP
         + Down-right below: SWAMP
         + Down-right down-left: MEADOW_2
         + Down-left below: SWAMP
         + Down-left down-left: SWAMP
         + Down-left up-left: MEADOW_2
         + Left down-left: MEADOW_3
         + Left up-left: SWAMP
         + Left above: SWAMP
         + Up-left up-left: SWAMP
         + Up-left above: MEADOW_3
       */

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Create the terrain used in the africa map
        var point0 = new Point(68, 68);

        map.setVegetationUpLeft(point0, SAVANNAH);
        map.setVegetationAbove(point0, SAVANNAH);
        map.setVegetationUpRight(point0, SAVANNAH);
        map.setVegetationDownRight(point0, SAVANNAH);
        map.setVegetationBelow(point0, SAVANNAH);
        map.setVegetationDownLeft(point0, SAVANNAH);

        map.setVegetationUpRight(point0.upLeft(), WATER);
        map.setVegetationAbove(point0.upRight(), WATER);
        map.setVegetationUpRight(point0.upRight(), WATER);
        map.setVegetationAbove(point0.right(), WATER);
        map.setVegetationUpRight(point0.right(), WATER);
        map.setVegetationDownRight(point0.right(), WATER);
        map.setVegetationBelow(point0.right(), WATER);
        map.setVegetationDownRight(point0.downRight(), WATER);
        map.setVegetationBelow(point0.downRight(), SAVANNAH);
        map.setVegetationDownLeft(point0.downRight(), WATER);
        map.setVegetationBelow(point0.downLeft(), WATER);
        map.setVegetationDownLeft(point0.downLeft(), WATER);
        map.setVegetationUpLeft(point0.downLeft(), SAVANNAH);
        map.setVegetationDownLeft(point0.left(), SAVANNAH);
        map.setVegetationUpLeft(point0.left(), SAVANNAH);
        map.setVegetationAbove(point0.left(), SAVANNAH);
        map.setVegetationUpLeft(point0.upLeft(), WATER);
        map.setVegetationAbove(point0.upLeft(), WATER);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        assertTrue(map.isBuildingAtPoint(point0));
        assertEquals(headquarter, map.getBuildingAtPoint(point0));
    }

    @Test
    public void testPlaceHeadquarterInWelt47Map() throws InvalidUserActionException {

    /*
         - Up left: SAVANNAH
         - Above: SAVANNAH
         - Up right: SAVANNAH
         - Down right: SAVANNAH
         - Below: SAVANNAH
         - Down left: SAVANNAH
         + Up-left up-right: WATER
         + Up-right above: WATER
         + Up-right up-right: WATER
         + Right above: WATER
         + Right up-right: WATER
         + Right down-right: WATER
         + Right below: WATER
         + Down-right down-right: WATER
         + Down-right below: SAVANNAH
         + Down-right down-left: WATER
         + Down-left below: WATER
         + Down-left down-left: WATER
         + Down-left up-left: SAVANNAH
         + Left down-left: SAVANNAH
         + Left up-left: SAVANNAH
         + Left above: SAVANNAH
         + Up-left up-left: WATER
         + Up-left above: WATER
       */

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Create the terrain used in the africa map
        var point0 = new Point(68, 68);

        map.setVegetationUpLeft(point0, MEADOW_2);
        map.setVegetationAbove(point0, MEADOW_2);
        map.setVegetationUpRight(point0, MEADOW_1);
        map.setVegetationDownRight(point0, MEADOW_3);
        map.setVegetationBelow(point0, MEADOW_3);
        map.setVegetationDownLeft(point0, MEADOW_2);

        map.setVegetationUpRight(point0.upLeft(), MEADOW_3);
        map.setVegetationAbove(point0.upRight(), FLOWER_MEADOW);
        map.setVegetationUpRight(point0.upRight(), FLOWER_MEADOW);
        map.setVegetationAbove(point0.right(), MEADOW_1);
        map.setVegetationUpRight(point0.right(), MEADOW_1);
        map.setVegetationDownRight(point0.right(), MEADOW_1);
        map.setVegetationBelow(point0.right(), MEADOW_3);
        map.setVegetationDownRight(point0.downRight(), SWAMP);
        map.setVegetationBelow(point0.downRight(), SWAMP);
        map.setVegetationDownLeft(point0.downRight(), MEADOW_2);
        map.setVegetationBelow(point0.downLeft(), SWAMP);
        map.setVegetationDownLeft(point0.downLeft(), SWAMP);
        map.setVegetationUpLeft(point0.downLeft(), MEADOW_2);
        map.setVegetationDownLeft(point0.left(), MEADOW_3);
        map.setVegetationUpLeft(point0.left(), SWAMP);
        map.setVegetationAbove(point0.left(), SWAMP);
        map.setVegetationUpLeft(point0.upLeft(), SWAMP);
        map.setVegetationAbove(point0.upLeft(), MEADOW_3);

        // Place headquarters
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        assertTrue(map.isBuildingAtPoint(point0));
        assertEquals(headquarter, map.getBuildingAtPoint(point0));
    }

    @Test
    public void testMonitoringEventWhenMilitaryBuildingOccupiedOutOfOrder() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place first barracks, but leave it unfinished & unoccupied
        var point1 = new Point(58, 68);
        var barracks0 = map.placeBuilding(new Barracks(player0), point1);

        // Place second barracks and wait for it to get occupied
        var point2 = new Point(78, 68);
        var barracks1 = map.placeBuilding(new Barracks(player0), point2);

        var road0 = map.placeAutoSelectedRoad(player0, barracks1.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks1);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks1);

        // Start monitoring
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        // Let the first barracks get occupied and verify that an event is sent with its discovered points
        var road1 = map.placeAutoSelectedRoad(player0, barracks0.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks0);

        monitor.clearEvents();

        var point3 = new Point(34, 68);

        assertFalse(player0.getDiscoveredLand().contains(point3));

        Utils.waitForMilitaryBuildingToGetPopulated(barracks0);

        // Verify that the event was sent and the points are discovered
        assertTrue(player0.getDiscoveredLand().contains(point3));
        assertEquals(monitor.getEvents().stream()
                .filter(gcl -> gcl.newDiscoveredLand().contains(point3))
                .count(),
                1);
    }

    @Test
    public void testDonkeyGoesBackToStorehouseWhenItsRoadIsRemoved() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Make sure there is a lot of wood for the sawmill to work on
        Utils.adjustInventoryTo(headquarter, WOOD, 500);

        // Place sawmill and connect it to the headquarters
        var point1 = new Point(82, 68);
        var sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter.getFlag());

        // Wait for the sawmill to get constructed and populated
        Utils.waitForBuildingToBeConstructed(sawmill0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill0);

        // Make the road get promoted to a main road
        for (int i = 0; i < 10_000; i++) {
            if (road0.isMainRoad()) {
                break;
            }

            if (sawmill0.getFlag().getStackedCargo().isEmpty()) {
                Utils.placeCargo(map, PLANK, sawmill0.getFlag(), headquarter);
            }

            map.stepTime();
        }

        assertTrue(road0.isMainRoad());

        // Wait for road to get a donkey assigned
        var donkey = Utils.waitForRoadToGetAssignedDonkey(road0);

        // Wait for the donkey to carry a cargo to the sawmill and be at the sawmill's flag
        for (int i = 0; i < 5000; i++) {
            if (donkey.getCargo() != null &&
                    donkey.getTarget().equals(sawmill0.getPosition()) &&
                    donkey.getPosition().equals(sawmill0.getFlag().getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertNotNull(donkey.getCargo());
        assertEquals(donkey.getTarget(), sawmill0.getPosition());
        assertEquals(donkey.getPosition(), sawmill0.getFlag().getPosition());

        // Wait for the donkey to go to the sawmill and deliver the cargo
        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, sawmill0.getPosition());

        // Verify that the donkey goes back to the headquarters when it's between the sawmill and its flag, and its road is removed
        map.stepTime();

        assertFalse(donkey.isExactlyAtPoint());
        assertEquals(donkey.getNextPoint(), sawmill0.getFlag().getPosition());

        map.removeRoad(road0);

        assertEquals(donkey.getTarget(), headquarter.getPosition());
        assertTrue(map.getWorkers().contains(donkey));

        Utils.fastForwardUntilWorkerReachesPoint(map, donkey, headquarter.getPosition());

        assertFalse(map.getWorkers().contains(donkey));
    }

    @Test
    public void testTwoFishermanFishAtSameSpotWhenThereIsOnlyOneFishLeft() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place a small lake
        var point1 = new Point(74, 68);
        Utils.surroundPointWithWater(point1, map);

        // Fix so that there is only one fish available
        Utils.adjustFishAvailable(map, point1.upLeft(), 0);
        Utils.adjustFishAvailable(map, point1.upRight(), 0);
        Utils.adjustFishAvailable(map, point1.right(), 0);
        Utils.adjustFishAvailable(map, point1.downRight(), 0);
        Utils.adjustFishAvailable(map, point1.downLeft(), 0);
        Utils.adjustFishAvailable(map, point1.left(), 1);

        // Place two fisheries, construct and occupy them
        var point2 = new Point(67, 63);
        var point3 = new Point(75, 63);

        var fishery0 = map.placeBuilding(new Fishery(player0), point2);
        var fishery1 = map.placeBuilding(new Fishery(player0), point3);

        Utils.constructHouses(fishery0, fishery1);

        Utils.occupyBuilding(new Fisherman(player0, map), fishery0);
        Utils.occupyBuilding(new Fisherman(player0, map), fishery1);

        // Wait for both fishermen to go out at the same time to the same spot
        var fishermen = Utils.waitForWorkersOutsideBuilding(Fisherman.class, 2, player0);

        var fisherman0 = fishermen.getFirst();
        var fisherman1 = fishermen.get(1);

        assertEquals(fishermen.size(), 2);
        assertEquals(fisherman0.getTarget(), point1.left());
        assertEquals(fisherman1.getTarget(), point1.left());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman0, point1.left());

        Utils.waitForFishermanToStopFishing(fisherman0, map);

        assertFalse(fisherman0.isFishing());
        assertFalse(fisherman1.isFishing());

        // Verify that only one fisherman got a fish and that both then go back to their fisheries

        assertTrue(
                (fisherman0.getCargo() != null && fisherman1.getCargo() == null) ||
                        (fisherman0.getCargo() == null && fisherman1.getCargo() != null)
        );
        assertTrue(
                (fisherman0.getTarget().equals(fishery0.getPosition()) && fisherman1.getTarget().equals(fishery1.getPosition())) ||
                        (fisherman0.getTarget().equals(fishery1.getPosition()) && fisherman1.getTarget().equals(fishery0.getPosition()))
        );

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman0, fisherman1);
    }

    @Test
    public void testSawmillDeliversDirectlyToShipyard() throws InvalidUserActionException {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place a small lake
        var point1 = new Point(74, 74);

        // Adjust the headquarters so it only has enough planks to construct the buildings and no wood
        Utils.adjustInventoryTo(headquarter, PLANK, 5);
        Utils.adjustInventoryTo(headquarter, WOOD, 0);

        // Disable the tree conservation program
        player0.disableTreeConservationProgram();

        // Place a shipyard
        var point2 = new Point(72, 68);
        var shipyard = map.placeBuilding(new Shipyard(player0), point2);

        // Place a sawmill
        var point3 = new Point(76, 68);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Connect them to the headquarters and wait for them to get constructed and occupied
        var road0 = map.placeAutoSelectedRoad(player0, shipyard.getFlag(), headquarter.getFlag());
        var road1 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), shipyard.getFlag());

        Utils.waitForBuildingsToBeConstructed(shipyard, sawmill);

        assertEquals(headquarter.getAmount(PLANK), 0);

        Utils.waitForNonMilitaryBuildingsToGetPopulated(shipyard, sawmill);

        // Wait for the sawmill to produce a plank and place it at its flag
        Utils.adjustInventoryTo(headquarter, WOOD, 1);

        Utils.waitForFlagToGetStackedCargo(map, sawmill.getFlag(), 1);

        assertEquals(sawmill.getFlag().getStackedCargo().getFirst().getMaterial(), PLANK);

        // Verify that the plank is delivered to the sawmill
        assertEquals(shipyard.getAmount(PLANK), 0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, road1.getCourier(), PLANK);

        assertEquals(road1.getCourier().getTarget(), shipyard.getPosition());

        for (int i = 0; i < 2000; i++) {
            assertNotEquals(road1.getCourier().getPosition(), headquarter.getPosition());

            if (road1.getCourier().getPosition().equals(shipyard.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertEquals(road1.getCourier().getPosition(), shipyard.getPosition());
        assertEquals(shipyard.getAmount(PLANK), 1);
    }

    @Test
    public void testAddingDefenderDoesNotInterruptOngoingFight() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1), 100, 100);

        // Place headquarters
        var point0 = new Point(60, 68);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        var point1 = new Point(30, 68);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Remove all soldiers for player 1 to prevent it from populating its barracks
        Utils.setReservedSoldiers(headquarter0, 0, 0, 0, 0, 0);
        Utils.setReservedSoldiers(headquarter1, 0, 0, 0, 0, 0);

        Utils.clearSoldiersFromInventory(headquarter0);
        Utils.clearSoldiersFromInventory(headquarter1);

        Utils.adjustInventoryTo(headquarter0, GENERAL, 2);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 2);

        // Place and connect barracks to get attacked
        var point2 = new Point(44, 68);
        var barracks = map.placeBuilding(new Barracks(player1), point2);

        var road = map.placeAutoSelectedRoad(player1, barracks.getFlag(), headquarter1.getFlag());

        // Wait for the barracks to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(barracks);

        Utils.waitForMilitaryBuildingToGetPopulated(barracks);

        // Evacuate the barracks and wait for the soldiers to go back to the headquarters
        barracks.evacuate();

        Utils.waitForInventoryToContain(headquarter1, GENERAL, 2);

        // Make player 1 defend as much as possible from surrounding buildings
        player1.setDefenseFromSurroundingBuildings(10);

        // Player 0 attacks player 1's barracks
        assertEquals(headquarter1.getAmount(GENERAL), 2);

        player0.attack(barracks, 1, AttackStrength.STRONG);

        // Wait for the attacker to reach the barracks and start fighting with a defender from player 1's headquarters
        var attacker = Utils.waitForSoldierOutsideBuilding(player0);

        assertEquals(attacker.getTarget(), barracks.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks.getFlag().getPosition());

        Utils.waitForSoldierToBeFighting(attacker, map);

        var defenderFighting = attacker.getOpponent();

        // Add a defender to the barracks and verify that it doesn't interrupt the fighting
        barracks.cancelEvacuation();

        var newDefender = new Soldier(player1, PRIVATE_RANK, map);

        map.placeWorker(newDefender, barracks.getFlag());

        newDefender.setTargetBuilding(barracks);

        Utils.fastForwardUntilWorkerReachesPoint(map, newDefender, barracks.getPosition());

        assertTrue(newDefender.isInsideBuilding());

        for (int i = 0; i < 20; i++) {
            assertTrue(attacker.isFighting());
            assertEquals(attacker.getOpponent(), defenderFighting);
            assertTrue(newDefender.isInsideBuilding());
            assertFalse(newDefender.isFighting());

            map.stepTime();
        }
    }

    @Test
    public void testMilitaryBuildingReceivesAlreadySentCoinEvenWithCoinsBlocked() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(58, 68);
        var barracks = map.placeBuilding(new Barracks(player0), point1);

        var road = map.placeAutoSelectedRoad(player0, barracks.getFlag(), headquarter.getFlag());

        Utils.waitForBuildingToBeConstructed(barracks);
        Utils.waitForMilitaryBuildingToGetPopulated(barracks);

        // Place coins in the headquarters
        Utils.adjustInventoryTo(headquarter, COIN, 10);

        // Wait for a coin to get delivered to the barracks
        var courier = road.getCourier();

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, COIN);

        // Stop promotions in the barracks
        barracks.disablePromotions();

        // Verify that the coin can still be delivered
        assertEquals(barracks.getAmount(COIN), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, barracks.getPosition());

        assertEquals(barracks.getAmount(COIN), 1);
        assertNull(courier.getCargo());
    }

    @Test
    public void testTearDownMilitaryBuildingWithPlannedBuildingCloseBy() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 100, 100);

        // Place headquarters
        var point0 = new Point(68, 68);
        var headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place barracks, connect it to the headquarters, and wait for it to get constructed and occupied
        var point1 = new Point(58, 68);
        var barracks = map.placeBuilding(new Barracks(player0), point1);

        var road = map.placeAutoSelectedRoad(player0, barracks.getFlag(), headquarter.getFlag());

        var point2 = new Point(50, 68);

        assertFalse(player0.getOwnedLand().contains(point2));

        Utils.waitForBuildingToBeConstructed(barracks);
        Utils.waitForMilitaryBuildingToGetPopulated(barracks);

        assertTrue(player0.getOwnedLand().contains(point2));

        // Place a planned building
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        // Verify that the planned building disappears when the barracks is torn down (and no exception is thrown)
        assertTrue(map.isBuildingAtPoint(point2));
        assertEquals(map.getBuildingAtPoint(point2), woodcutter);

        barracks.tearDown();

        assertFalse(player0.getOwnedLand().contains(point2));
        assertFalse(map.isBuildingAtPoint(point2));
        assertNotEquals(map.getBuildingAtPoint(point2), woodcutter);
    }
}
