package org.appland.settlers.test;

import org.appland.settlers.model.Armory;
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
import java.util.List;

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
        Road road0 = map.placeAutoSelectedRoad(player, new Point(430, 200), new Point(434, 200));

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

        /* Verify that trying to get possible road connections from a point outside the map throws
        * invalid user action exception
        */
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
        Point point02 = new Point(5, 23);
        Building guardHouse0 = map.placeBuilding(new GuardHouse(player0), point02);

        Point point03 = new Point(21, 5);
        Building guardHouse1 = map.placeBuilding(new GuardHouse(player0), point03);

        /* Finish construction of both guard houses */
        Utils.constructHouse(guardHouse0);
        Utils.constructHouse(guardHouse1);

        /* Connect the first guard house to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), guardHouse0.getFlag());

        /* Verify that the discovered area is only extended around the guard house that gets occupied */
        Point point04 = new Point(5, 29);
        Point point05 = new Point(29, 5);

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
        Point point1 = new Point(6, 22);
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
        Point point1 = new Point(6, 22);
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
        Point point1 = new Point(6, 22);
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

            if (woodcutter0.ready()) {
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

        assertTrue(woodcutter0.ready());
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
}
