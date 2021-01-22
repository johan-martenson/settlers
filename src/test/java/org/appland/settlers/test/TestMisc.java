package org.appland.settlers.test;

import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Donkey;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.Fortress;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GuardHouse;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
import org.appland.settlers.model.InvalidRouteException;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Scout;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Woodcutter;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.Material.COURIER;
import static org.appland.settlers.model.Material.DONKEY;
import static org.appland.settlers.model.Material.GENERAL;
import static org.appland.settlers.model.Material.OFFICER;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.PRIVATE_FIRST_CLASS;
import static org.appland.settlers.model.Material.SERGEANT;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.OFFICER_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_FIRST_CLASS_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        /* Starting new game */
        Player player = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 500, 250);

        /* Placing headquarter */
        Point point0 = new Point(429, 201);
        Headquarter headquarter0 = map.placeBuilding(new org.appland.settlers.model.Headquarter(player), point0);

        /* Place flag */
        Point point1 = new Point(434, 200);
        Flag flag0 = map.placeFlag(player, point1);

        /* Place automatic road between flag and headquarter's flag */
        Road road0 = map.placeAutoSelectedRoad(player, headquarter0.getFlag().getPosition(), point1);

        /* Place woodcutter by the flag */
        Woodcutter woodcutter0 = map.placeBuilding(new org.appland.settlers.model.Woodcutter(player), flag0.getPosition().upLeft());

        /* Wait for the road to get an assigned courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Fast forward a bit until the courier is carrying a cargo to deliver to the woodcutter */
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

        /* Remove the flag and cause the woodcutter to get torn down */
        map.removeFlag(map.getFlagAtPoint(point1));

        assertTrue(map.getWorkers().size() >= 2);

        /* Verify that the courier goes back to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertEquals(courier.getPosition(), headquarter0.getPosition());
        assertFalse(map.getWorkers().contains(courier));
    }

    @Test
    public void testScoutReturnsWhenFlagRemainsButRoadHasBeenRemoved() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 500, 250);

        /* Placing headquarter */
        Point point0 = new Point(429, 201);
        Headquarter headquarter0 = map.placeBuilding(new org.appland.settlers.model.Headquarter(player), point0);

        /* Place flag */
        Point point1 = new Point(434, 200);
        Flag flag0 = map.placeFlag(player, point1);

        /* Call scout */
        flag0.callScout();

        /* Create a road that connects the flag with the headquarter's flag */
        Road road0 = map.placeAutoSelectedRoad(player, headquarter0.getFlag(), flag0);

        /* Wait for a scout to appear */
        Scout scout = Utils.waitForWorkersOutsideBuilding(Scout.class, 1, player).get(0);

        /* Wait the scout to get to the flag */
        assertEquals(scout.getTarget(), flag0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag0.getPosition());

        assertEquals(scout.getPosition(), flag0.getPosition());

        /* Wait for the scout to continue away from the flag */
        Utils.fastForward(10, map);

        assertNotEquals(scout.getPosition(), flag0.getPosition());

        /* Remove the road so the scout has no way back using roads */
        map.removeRoad(road0);

        /* Wait for the scout to get back to the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, scout, flag0.getPosition());

        assertEquals(scout.getPosition(), flag0.getPosition());

        /* Verify that the scout goes back to the headquarter */
        assertEquals(scout.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, scout, headquarter0.getPosition());

        assertEquals(scout.getPosition(), headquarter0.getPosition());
    }

    @Test
    public void testPlaceRoadToSamePointOnEdgeOfScreen() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 500, 250);

        /* Placing headquarter */
        Point point0 = new Point(429, 201);
        Headquarter headquarter0 = map.placeBuilding(new org.appland.settlers.model.Headquarter(player), point0);

        /* Verify that placing a road to the same point on the edge of the screen causes an invalid endpoint exception */
        Point point1 = new Point(0, 0);
        try {
            Road road0 = map.placeAutoSelectedRoad(player, point1, point1);

            fail();
        } catch (InvalidEndPointException e) {

        }
    }

    @Test
    public void testPlaceFirstBuildingOnEdgeOfScreen() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 500, 250);

        /* Verify that it's not possible to place a headquarter on the edge of the screen */
        Point point0 = new Point(0, 2);

        try {
            Headquarter headquarter0 = map.placeBuilding(new org.appland.settlers.model.Headquarter(player), point0);

            fail();
        } catch (InvalidUserActionException e) {
        }
    }

    @Test
    public void testGetPossibleAdjacentRoadConnectionsIncludingEndpointsOutsideMap() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 100, 100);

        /* Verify that trying to get possible road connections from a point outside the map throws invalid user action exception */
        try {
            map.getPossibleAdjacentRoadConnectionsIncludingEndpoints(player, new Point(174, 132));

            fail();
        } catch (InvalidUserActionException e) {

        }
    }

    @Test
    public void testPlaceRoadWithoutPoints() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 500, 250);

        /* Placing headquarter */
        Point point0 = new Point(429, 201);
        Headquarter headquarter0 = map.placeBuilding(new org.appland.settlers.model.Headquarter(player), point0);

        /* Verify that placing a road without any points throws an invalid user action exception */
        try {
            map.placeRoad(player, new ArrayList<>());

            fail();
        } catch (InvalidUserActionException e) {}
    }

    @Test
    public void testUnoccupiedMilitaryBuildingDoesNotIncreaseDiscoveredArea() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);

        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point01 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point01);

        /* Place guard houses */
        Point point02 = new Point(5, 13);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point02);

        Point point03 = new Point(19, 5);
        Building guardHouse1 = map.placeBuilding(new GuardHouse(player0), point03);

        /* Finish construction of both guard houses */
        Utils.constructHouse(guardHouse0);
        Utils.constructHouse(guardHouse1);

        /* Connect the first guard house to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Verify that the discovered area is only extended around the guard house that gets occupied */
        Point point04 = new Point(5, 23);
        Point point05 = new Point(33, 5);

        assertFalse(player0.getDiscoveredLand().contains(point04));
        assertFalse(player0.getDiscoveredLand().contains(point05));

        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertNotNull(military);

        /* Verify that the discovered area is only extended around the first guardhouse and not the second */
        assertEquals(military.getTarget(), guardHouse0.getPosition());
        assertFalse(player0.getDiscoveredLand().contains(point04));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, guardHouse0.getPosition());

        assertTrue(player0.getDiscoveredLand().contains(point04));
        assertFalse(player0.getDiscoveredLand().contains(point05));
    }

    @Test
    public void testBuildingWhereConstructionHasNotStartedIsAtZeroPercentProgress() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 12);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Verify that the construction is at zero progress */
        assertEquals(armory0.getConstructionProgress(), 0);
    }

    @Test
    public void testConstructionProgressNeverGoesBackwards() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 12);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Verify that the construction progress never goes backwards */
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

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place armory */
        Point point1 = new Point(6, 12);
        Building armory0 = map.placeBuilding(new Armory(player0), point1);

        /* Construct the building */
        Utils.constructHouse(armory0);

        /* Verify that the construction is at hundred progress */
        assertEquals(armory0.getConstructionProgress(), 100);
    }

    @Test
    public void testNoMonitoringEventWithEmptyPathForStorageWorker() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Place a woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Wait for the woodcutter to get constructed */
        GameChangesList lastEvent = null;
        for (int i = 0; i < 1000; i++) {

            if (woodcutter0.isReady()) {
                break;
            }

            if (monitor.getLastEvent() != null) {

                GameChangesList thisEvent = monitor.getLastEvent();

                if (!thisEvent.equals(lastEvent)) {

                    for (Worker worker : monitor.getLastEvent().getWorkersWithNewTargets()) {
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

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        map.stepTime();

        /* Place a woodcutter */
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        /* Wait for the woodcutter to get constructed and populated */
        Utils.waitForBuildingToBeConstructed(woodcutter0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        Worker woodcutterWorker = woodcutter0.getWorker();

        map.stepTime();

        /* Place a tree that the woodcutter can cut down */
        Point point2 = new Point(14, 12);
        Tree tree0 = map.placeTree(point2);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        assertEquals(monitor.getEvents().size(), 0);

        /* Verify that an event is sent when the worker goes out to cut down the tree */
        assertTrue(woodcutterWorker.isInsideBuilding());

        int amountEvents = monitor.getEvents().size();

        Utils.waitForWorkerToBeOutside(woodcutterWorker, map);

        assertEquals(woodcutterWorker.getTarget(), tree0.getPosition());
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertTrue(monitor.getEvents().size() > amountEvents);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertTrue(gameChangesList.getWorkersWithNewTargets().contains(woodcutterWorker));
    }

    @Test
    public void testUnoccupiedMilitaryBuildingIsDestroyedWhenEnemyBarracksIsOccupied() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Verify that player 1's barracks is torn down when player 0's barracks gets occupied */
        assertTrue(barracks1.isReady());
        assertFalse(barracks1.isOccupied());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        assertTrue(barracks1.isBurningDown());
        assertFalse(map.isFlagAtPoint(barracks1.getFlag().getPosition()));
    }

    @Test
    public void testEvacuationIsDisabledWhenPlayerTakesOverBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Empty barracks 1 */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        barracks1.evacuate();

        Military military = Utils.waitForMilitaryOutsideBuilding(player1);

        assertEquals(military.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter1.getPosition());

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);

        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that evacuation is disabled when the attacker takes over the building */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);
        assertEquals(barracks1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(barracks1.isReady());
        assertTrue(barracks1.isEvacuated());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertFalse(barracks1.isEvacuated());
        assertEquals(barracks1.getPlayer(), player0);
        assertTrue(barracks1.getHostedMilitary().contains(attacker));
        assertTrue(attacker.isInsideBuilding());
        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
    }

    @Test
    public void testAllRanksCanReturnToStorage() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player);
        GameMap map = new GameMap(players, 100, 100);

        /* Placing headquarter */
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new org.appland.settlers.model.Headquarter(player), point0);

        /* Place fortress */
        Point point1 = new Point(20, 16);
        Fortress fortress0 = map.placeBuilding(new Fortress(player), point1);

        /* Connect the fortress with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player, fortress0.getFlag(), headquarter0.getFlag());

        /* Ensure that there is one soldier of each rank in the headquarter */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 1);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 1);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 1);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 1);

        /* Wait for the fortress to get constructed and occupied */
        Utils.waitForBuildingToBeConstructed(fortress0);
        Utils.waitForMilitaryBuildingToGetPopulated(fortress0, 5);

        /* Verify that when the fortress is burned down, all of the soldiers go back and get stored properly */
        assertEquals(headquarter0.getAmount(PRIVATE), 0);
        assertEquals(headquarter0.getAmount(PRIVATE_FIRST_CLASS), 0);
        assertEquals(headquarter0.getAmount(SERGEANT), 0);
        assertEquals(headquarter0.getAmount(OFFICER), 0);
        assertEquals(headquarter0.getAmount(GENERAL), 0);

        fortress0.tearDown();

        List<Military> soldiers = Utils.waitForWorkersOutsideBuilding(Military.class, 5, player);

        boolean stillOnMap;
        for (int i = 0; i < 5000; i++) {

            stillOnMap = false;

            for (Military soldier : soldiers) {
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

        for (Military soldier : soldiers) {
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

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 1, barracks1);

        /* Place a second barracks for player 1 */
        Point point4 = new Point(13, 15);
        Barracks barracks2 = map.placeBuilding(new Barracks(player1), point4);

        /* Construct the new barracks */
        Utils.constructHouse(barracks2);

        /* Remove all soldiers from the headquarter */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Place a road that connects the two barracks for player 1 */
        Road road0 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), barracks2.getFlag());

        /* Empty barracks 1 */
        Road road1 = map.placeAutoSelectedRoad(player1, barracks1.getFlag(), headquarter1.getFlag());

        barracks1.evacuate();

        Military military = Utils.waitForMilitaryOutsideBuilding(player1);

        assertEquals(military.getTarget(), headquarter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, military, headquarter1.getPosition());

        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);

        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

        /* Verify that the second barracks is destroyed, its road, and the road between the barracks are removed */
        Road road2 = map.getRoad(barracks2.getPosition(), barracks2.getFlag().getPosition());

        assertTrue(map.getRoads().contains(road0));
        assertTrue(map.getRoads().contains(road2));
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);
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
        assertTrue(player0.getLandInPoints().contains(barracks2.getPosition()));
        assertTrue(player0.getLandInPoints().contains(barracks2.getFlag().getPosition()));
        assertFalse(player1.isWithinBorder(barracks2.getPosition()));
        assertTrue(barracks2.isBurningDown());
    }

    @Test
    public void testCourierDeliveringToBuildingMakesCargoDisappearIfTargetUnderConstructionBuildingIsTornDownAndReturnToStorageIsNotPossible() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);

        List<Player> players = new LinkedList<>();

        players.add(player0);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Assign builder */
        Utils.assignBuilder(woodcutter);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Wait for a courier to come to the road */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /* Wait for the courier to carry a cargo to the woodcutter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getTarget(), woodcutter);

        /* Verify that the courier goes to the woodcutter and the cargo disappears if the woodcutter is torn down and it cannot be returned */
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
    public void testCourierDeliveringToBuildingMakesCargoDisappearIfPlannedTargetBuildingIsTornDownAndReturnToStorageIsNotPossible() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);

        List<Player> players = new LinkedList<>();

        players.add(player0);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(15, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter with the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Wait for a courier to come to the road */
        Courier courier = Utils.waitForWorkerOutsideBuilding(Courier.class, player0);

        /* Wait for the courier to carry a cargo to the woodcutter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getTarget(), woodcutter);

        /* Verify that the courier goes to the woodcutter and the cargo disappears if the woodcutter is torn down and it cannot be returned */
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
    public void testCourierDeliveringToFlagMakesCargoDisappearIfTargetBuildingIsTornDownAndReturnToStorageIsNotPossible() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);

        List<Player> players = new LinkedList<>();

        players.add(player0);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(17, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Place flag */
        Point point2 = new Point(14, 4);
        Flag flag0 = map.placeFlag(player0, point2);

        /* Connect the headquarter with the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Connect the flag with the woodcutter */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter.getFlag());

        /* Wait for the first road to get assigned a courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier to carry a cargo intended for the woodcutter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        map.stepTime();

        assertFalse(courier.isExactlyAtPoint());

        /* Verify that the courier goes to the flag and the cargo disappears if the woodcutter is torn down and it cannot be returned */
        Cargo cargo = courier.getCargo();

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

    @Test
    public void testAttackerCapturesAreaWithAlreadyDestroyedBuilding() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarter */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Place woodcutter close to barracks 1 */
        Point point4 = new Point(26, 14);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player1), point4);

        /* Finish construction */
        Utils.constructHouses(barracks0, barracks1, woodcutter0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1);

        /* Go forward a bit to time it so that the woodcutter is still on the map when the soldier takes over the building */
        Utils.fastForward(30, map);

        /* Find the military that was chosen to attack */
        Military attacker = Utils.findMilitaryOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Tear down the woodcutter */
        woodcutter0.tearDown();

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Verify that player 1's barracks is in player 1's border and not player 0's */
        Utils.verifyPointIsNotWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsWithinBorder(player1, barracks1.getPosition());

        /* Wait for the attacker to return to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Wait for the attacker to go to the barracks */
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(woodcutter0.isDestroyed());
        assertTrue(map.getBuildings().contains(woodcutter0));

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        /* Verify that the border is updated to include the captured building and that it's not in player 1's border anymore */
        assertTrue(map.getBuildings().contains(woodcutter0));
        assertTrue(woodcutter0.isDestroyed());
        assertTrue(player0.getLandInPoints().contains(woodcutter0.getPosition()));

        Utils.verifyPointIsWithinBorder(player0, barracks1.getPosition());
        Utils.verifyPointIsNotWithinBorder(player1, barracks1.getPosition());
        assertEquals(player0.getPlayerAtPoint(barracks1.getPosition()), player0);
    }

    @Test
    public void testRemovingFlagWithCargoOnItResetsPromisedDelivery() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);

        List<Player> players = new LinkedList<>();

        players.add(player0);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(14, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place woodcutter */
        Point point2 = new Point(17, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        /* Place road to connect the headquarter with the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place road to connect the flag with the woodcutter */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter.getFlag());

        /* Wait for courier to get assigned to the first road */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier to hold a cargo for the woodcutter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getTarget(), woodcutter);

        Cargo cargo = courier.getCargo();

        /* Wait for the courier to place the cargo on the flag */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition());

        assertNull(courier.getCargo());
        assertTrue(flag0.getStackedCargo().contains(cargo));

        /* Remove the flag so the cargo disappears and the promise to deliver it is broken */
        map.removeFlag(flag0);

        /* Place a new road to connect the headquarter directly with the woodcutter */
        Road road2 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Verify that the woodcutter gets all deliveries it needs and gets constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter);

        assertTrue(woodcutter.isReady());
    }

    @Test
    public void testRemovingRoadWithCourierCarryingCargoResetsPromisedDelivery() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);

        List<Player> players = new LinkedList<>();

        players.add(player0);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(14, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place woodcutter */
        Point point2 = new Point(17, 5);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point2);

        /* Place road to connect the headquarter with the flag */
        Road road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place road to connect the flag with the woodcutter */
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter.getFlag());

        /* Wait for courier to get assigned to the first road */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Wait for the courier to hold a cargo for the woodcutter */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier);

        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo().getTarget(), woodcutter);

        Cargo cargo = courier.getCargo();

        /* Wait for the courier to reach the middle of the road */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, flag0.getPosition().left());

        assertNotNull(courier.getCargo());

        /* Remove the flag so the cargo disappears and the promise to deliver it is broken */
        map.removeFlag(flag0);

        /* Place a new road to connect the headquarter directly with the woodcutter */
        Road road2 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        /* Verify that the woodcutter gets all deliveries it needs and gets constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter);

        assertTrue(woodcutter.isReady());
    }

    @Test
    public void testCannotPlaceHeadquarterTooCloseToRightEdgeOfMap() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Verify that it's not possible to place a headquarter so that it's flag will be on the border */
        Point point0 = new Point(7, 7);
        try {
            map.placeBuilding(new Headquarter(player0), point0);
            fail();
        } catch (InvalidUserActionException e) {}

        assertEquals(map.getBuildings().size(), 0);
    }

    @Test
    public void testCannotPlaceHeadquarterTooCloseToBottomEdgeOfMap() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Verify that it's not possible to place a headquarter so that it's flag will be on the border */
        Point point0 = new Point(7, 3);
        try {
            map.placeBuilding(new Headquarter(player0), point0);

            fail();
        } catch (InvalidUserActionException e) {}

        assertEquals(map.getBuildings().size(), 0);
    }

    @Test
    public void testCannotRemoveFlagThatIsNull() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 10, 10);

        /* Verify that it's not possible to remove a flag that is null */
        try {
            map.removeFlag(null);

            fail();
        } catch (InvalidUserActionException e) {}

        assertEquals(map.getBuildings().size(), 0);
    }

    @Test
    public void testCannotRemoveRoadFromDriveway() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarter */
        Point point0 = new Point(9, 9);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(13, 9);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Connect the woodcutter with the headquarter */
        Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter.getFlag());

        /* Wait for the woodcutter to get constructed */
        Utils.waitForBuildingToBeConstructed(woodcutter);

        /* Verify that it's not possible to remove a driveway from a house */
        Road road1 = map.getRoad(woodcutter.getPosition(), woodcutter.getFlag().getPosition());

        try {
            map.removeRoad(road1);

            fail();
        } catch (InvalidUserActionException e) {}

        assertTrue(map.getRoads().contains(road1));
    }

    @Test
    public void testPushOutDonkey() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(68, 68);
        Headquarter headquarter = map.placeBuilding(new org.appland.settlers.model.Headquarter(player0), point0);

        /* Push out donkeys */
        headquarter.pushOutAll(DONKEY);

        /* Verify that donkeys can be pushed out */
        Utils.waitForWorkerOutsideBuilding(Donkey.class, player0);
    }

    @Test
    public void testPushOutCourier() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(68, 68);
        Headquarter headquarter = map.placeBuilding(new org.appland.settlers.model.Headquarter(player0), point0);

        /* Push out courier */
        headquarter.pushOutAll(COURIER);

        /* Verify that couriers can be pushed out */
        Utils.waitForWorkerOutsideBuilding(Courier.class, player0);
    }

    @Test
    public void testPushOutPrivate() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(68, 68);
        Headquarter headquarter = map.placeBuilding(new org.appland.settlers.model.Headquarter(player0), point0);

        /* Push out private */
        headquarter.pushOutAll(PRIVATE);

        /* Verify that couriers can be pushed out */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertEquals(military.getRank(), PRIVATE_RANK);
    }

    @Test
    public void testPushOutPrivateFirstClass() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(68, 68);
        Headquarter headquarter = map.placeBuilding(new org.appland.settlers.model.Headquarter(player0), point0);

        /* Put a private first class into the headquarter */
        Utils.adjustInventoryTo(headquarter, PRIVATE_FIRST_CLASS, 1);

        /* Push out private first class */
        headquarter.pushOutAll(PRIVATE_FIRST_CLASS);

        /* Verify that private first class can be pushed out */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertEquals(military.getRank(), PRIVATE_FIRST_CLASS_RANK);
    }

    @Test
    public void testPushOutSergeant() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(68, 68);
        Headquarter headquarter = map.placeBuilding(new org.appland.settlers.model.Headquarter(player0), point0);

        /* Put a sergeant into the headquarter */
        Utils.adjustInventoryTo(headquarter, SERGEANT, 1);

        /* Push out sergeant */
        headquarter.pushOutAll(SERGEANT);

        /* Verify that sergeant can be pushed out */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertEquals(military.getRank(), SERGEANT_RANK);
    }

    @Test
    public void testPushOutOfficer() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(68, 68);
        Headquarter headquarter = map.placeBuilding(new org.appland.settlers.model.Headquarter(player0), point0);

        /* Put a officer into the headquarter */
        Utils.adjustInventoryTo(headquarter, OFFICER, 1);

        /* Push out officer */
        headquarter.pushOutAll(OFFICER);

        /* Verify that officer can be pushed out */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertEquals(military.getRank(), OFFICER_RANK);
    }

    @Test
    public void testPushOutGeneral() throws InvalidUserActionException, InvalidEndPointException, InvalidRouteException {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarter */
        Point point0 = new Point(68, 68);
        Headquarter headquarter = map.placeBuilding(new org.appland.settlers.model.Headquarter(player0), point0);

        /* Put a general into the headquarter */
        Utils.adjustInventoryTo(headquarter, GENERAL, 1);

        /* Push out general */
        headquarter.pushOutAll(GENERAL);

        /* Verify that general can be pushed out */
        Military military = Utils.waitForMilitaryOutsideBuilding(player0);

        assertEquals(military.getRank(), GENERAL_RANK);
    }
}
