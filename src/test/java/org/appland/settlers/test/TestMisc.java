package org.appland.settlers.test;

import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.Courier;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GuardHouse;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.InvalidEndPointException;
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
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestMisc {

    /*
    TODO:
     - Coin delivery to upgraded building
     - Evacuation of military building is disabled when a player takes over a military building

     */

    @Test
    public void testRemoveRoadWhenCourierGoesToBuildingToDeliverCargo() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", java.awt.Color.BLUE);
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

        assertEquals(map.getWorkers().size(), 2);
        assertNotNull(courier.getCargo());
        assertEquals(courier.getTarget(), woodcutter0.getPosition());
        assertEquals(courier.getLastPoint(), headquarter0.getFlag().getPosition().right());
        assertEquals(courier.getNextPoint(), woodcutter0.getPosition().downRight());

        /* Remove the flag and cause the woodcutter to get torn down */
        map.removeFlag(map.getFlagAtPoint(point1));

        assertEquals(map.getWorkers().size(), 2);

        /* Verify that the courier goes back to the headquarter */
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertEquals(courier.getPosition(), headquarter0.getPosition());
        assertFalse(map.getWorkers().contains(courier));
    }

    @Test
    public void testScoutReturnsWhenFlagRemainsButRoadHasBeenRemoved() throws Exception {

        /* Starting new game */
        Player player = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

            if (!armory0.underConstruction()) {
                break;
            }

            map.stepTime();
        }
    }

    @Test
    public void testFullyConstructedBuildingIsAtHundredPercentProgress() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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

        /* Verify that the construction is at hundred  progress */
        assertEquals(armory0.getConstructionProgress(), 100);
    }

    @Test
    public void testNoMonitoringEventWithEmptyPathForStorageWorker() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
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
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(9, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(45, 5);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(29, 5);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

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
        Headquarter headquarter0 = new Headquarter(player0);
        Point point0 = new Point(9, 5);
        map.placeBuilding(headquarter0, point0);

        /* Place player 1's headquarter */
        Headquarter headquarter1 = new Headquarter(player1);
        Point point1 = new Point(37, 15);
        map.placeBuilding(headquarter1, point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = new Barracks(player0);
        map.placeBuilding(barracks0, point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = new Barracks(player1);
        map.placeBuilding(barracks1, point3);

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
}
