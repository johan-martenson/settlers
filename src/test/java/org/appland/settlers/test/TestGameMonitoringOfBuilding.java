package org.appland.settlers.test;

import org.appland.settlers.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestGameMonitoringOfBuilding {

    @Test
    public void testMonitoringEventWhenBeerIsAddedToHeadquarter() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Wait for the road to get assigned a courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* No house updated event is sent when the headquarters receives cargo */
        Cargo beerCargo0 = Utils.placeCargo(map, BEER, flag0, headquarter0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, beerCargo0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that a house updated event is sent when the headquarters receives cargo */
        Cargo beerCargo1 = Utils.placeCargo(map, BEER, flag0, headquarter0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, beerCargo1);

        GameChangesList lastGameChangesList = monitor.getLastEvent();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {
                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(headquarter0));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);

        /* Turn off detailed monitoring */
        player0.removeDetailedMonitoring(headquarter0);

        /* Verify that no house updated event is sent when the headquarters receives cargo */
        Cargo beerCargo2 = Utils.placeCargo(map, BEER, flag0, headquarter0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, beerCargo2);

        lastGameChangesList = monitor.getLastEvent();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenPlankIsRemovedFromHeadquarter() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place woodcutter */
        Point point2 = new Point(9, 5);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Wait for the road to get assigned a courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* No house updated event is sent when the first plank leaves the headquarters */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that a house updated event is sent when the second plank leaves the headquarters */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, courier);

        GameChangesList lastGameChangesList = monitor.getLastEvent();

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, farm0.getPosition());

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {

                assertEquals(gameChangesList.getChangedBuildings().size(), 1);

                if (gameChangesList.getChangedBuildings().contains(farm0)) {
                    continue;
                }

                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(headquarter0));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);

        /* Turn off detailed monitoring */
        player0.removeDetailedMonitoring(headquarter0);

        /* Verify that no house updated event is sent when the headquarters receives cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, courier);

        lastGameChangesList = monitor.getLastEvent();

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenBuilderLeavesHeadquarter() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place woodcutter */
        Point point2 = new Point(9, 5);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, BUILDER, 10);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* No house updated event is sent when the first builder leaves the headquarters */
        Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }

        /* Wait for the builder to return to the headquarters */
        farm0.tearDown();

        Utils.waitForNoWorkerOutsideBuilding(Builder.class, player0);

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Place a forester and connect it to the headquarters */
        Point point3 = new Point (14, 8);
        ForesterHut foresterHut0 = map.placeBuilding(new ForesterHut(player0), point3);

        Road road1 = map.placeAutoSelectedRoad(player0, foresterHut0.getFlag(), headquarter0.getFlag());

        /* Verify that a house updated event is sent when the second builder leaves the headquarters */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {

                assertEquals(gameChangesList.getChangedBuildings().size(), 1);

                if (gameChangesList.getChangedBuildings().contains(farm0)) {
                    continue;
                }

                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(headquarter0));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);

        /* Turn off detailed monitoring */
        player0.removeDetailedMonitoring(headquarter0);

        /* Place a third building and connect it to the headquarters */
        Point point4 = new Point(4, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point4);

        Road road2 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Verify that no house updated event is sent when the third builder leaves the headquarters */
        lastGameChangesList = monitor.getLastEvent();

        Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenPlankIsAddedToBuildingUnderConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        /* Place woodcutter */
        Point point2 = new Point(9, 5);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Wait for the road to get assigned a courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* No house updated event is sent when the first plank leaves the headquarters */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(farm0));
        }

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(farm0);

        /* Verify that a house updated event is sent when the second plank gets to the farm */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, courier);

        GameChangesList lastGameChangesList = monitor.getLastEvent();

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, farm0.getPosition());

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {

                assertEquals(gameChangesList.getChangedBuildings().size(), 1);

                if (gameChangesList.getChangedBuildings().contains(headquarter0)) {
                    continue;
                }

                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(farm0));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);

        /* Turn off detailed monitoring */
        player0.removeDetailedMonitoring(headquarter0);

        /* Verify that no house updated event is sent when the third plank gets to the farm */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, courier);

        lastGameChangesList = monitor.getLastEvent();

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenReservedLimitIsRaisedAndSoldierInInventoryBecomesHosted() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set the reserved soldiers for the headquarters */
        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 0);
        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_FIRST_CLASS_RANK, 0);
        headquarter0.setReservedSoldiers(Military.Rank.SERGEANT_RANK, 0);
        headquarter0.setReservedSoldiers(Military.Rank.OFFICER_RANK, 0);
        headquarter0.setReservedSoldiers(Military.Rank.GENERAL_RANK, 0);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* No house updated event is sent when the reserve limit is raised the first time */
        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 1);

        assertEquals(headquarter0.getReservedSoldiers(Military.Rank.PRIVATE_RANK), 1);
        assertEquals(headquarter0.getHostedSoldiersWithRank(Military.Rank.PRIVATE_RANK), 1);
        assertEquals(headquarter0.getAmount(PRIVATE), 9);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(headquarter0));
        }

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that a house updated event is sent when the reserve limit is raised the second time */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Military.Rank.PRIVATE_RANK), 3);
        assertEquals(headquarter0.getHostedSoldiersWithRank(Military.Rank.PRIVATE_RANK), 3);
        assertEquals(headquarter0.getAmount(PRIVATE), 7);

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {
                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(headquarter0));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);

        /* Turn off detailed monitoring */
        player0.removeDetailedMonitoring(headquarter0);

        /* Verify that no house updated event is sent when the third plank gets to the farm */
        lastGameChangesList = monitor.getLastEvent();

        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 5);

        assertEquals(headquarter0.getReservedSoldiers(Military.Rank.PRIVATE_RANK), 5);
        assertEquals(headquarter0.getHostedSoldiersWithRank(Military.Rank.PRIVATE_RANK), 5);
        assertEquals(headquarter0.getAmount(PRIVATE), 5);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenReservedLimitIsLoweredAndHostedSoldierMovesToInventory() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);

        /* Set the reserved soldiers for the headquarters */
        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 10);
        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_FIRST_CLASS_RANK, 0);
        headquarter0.setReservedSoldiers(Military.Rank.SERGEANT_RANK, 0);
        headquarter0.setReservedSoldiers(Military.Rank.OFFICER_RANK, 0);
        headquarter0.setReservedSoldiers(Military.Rank.GENERAL_RANK, 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* No house updated event is sent when the reserve limit is raised the first time */
        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 5);

        assertEquals(headquarter0.getReservedSoldiers(Military.Rank.PRIVATE_RANK), 5);
        assertEquals(headquarter0.getHostedSoldiersWithRank(Military.Rank.PRIVATE_RANK), 5);
        assertEquals(headquarter0.getAmount(PRIVATE), 5);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(headquarter0));
        }

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that a house updated event is sent when the reserve limit is raised the second time */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Military.Rank.PRIVATE_RANK), 3);
        assertEquals(headquarter0.getHostedSoldiersWithRank(Military.Rank.PRIVATE_RANK), 3);
        assertEquals(headquarter0.getAmount(PRIVATE), 7);

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {
                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(headquarter0));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);

        /* Turn off detailed monitoring */
        player0.removeDetailedMonitoring(headquarter0);

        /* Verify that no house updated event is sent when the third plank gets to the farm */
        lastGameChangesList = monitor.getLastEvent();

        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 1);

        assertEquals(headquarter0.getReservedSoldiers(Military.Rank.PRIVATE_RANK), 1);
        assertEquals(headquarter0.getHostedSoldiersWithRank(Military.Rank.PRIVATE_RANK), 1);
        assertEquals(headquarter0.getAmount(PRIVATE), 9);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitorEventWhenEvacuatingMilitaryBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        WatchTower watchTower = map.placeBuilding(new WatchTower(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, watchTower.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(watchTower);

        /* Let one soldier enter the building to make it occupied */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);

        Military military0 = Utils.waitForWorkerOutsideBuilding(Military.class, player0);

        Utils.fastForwardUntilWorkerReachesPoint(map, military0, watchTower.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Add detailed monitoring of the house */
        player0.addDetailedMonitoring(watchTower);

        /* Verify that there is an event when the house is evacuated */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        watchTower.evacuate();

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {
                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(watchTower));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);
    }

    @Test
    public void testMonitorEventWhenStoppingEvacuationOfMilitaryBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        WatchTower watchTower = map.placeBuilding(new WatchTower(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, watchTower.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(watchTower);

        /* Evacuate the house */
        watchTower.evacuate();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Add detailed monitoring of the house */
        player0.addDetailedMonitoring(watchTower);

        /* Verify that there is an event when the evacuated is canceled */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        watchTower.cancelEvacuation();

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {
                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(watchTower));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);
    }

    @Test
    public void testMonitorEventWhenPromotionsEnabledInMilitaryBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        WatchTower watchTower = map.placeBuilding(new WatchTower(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, watchTower.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(watchTower);

        /* Disable promotions in the house */
        watchTower.disablePromotions();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Add detailed monitoring of the house */
        player0.addDetailedMonitoring(watchTower);

        /* Verify that there is an event when promotions are enabled */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        watchTower.enablePromotions();

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {
                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(watchTower));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);
    }

    @Test
    public void testMonitorEventWhenPromotionsDisabledInMilitaryBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        WatchTower watchTower = map.placeBuilding(new WatchTower(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, watchTower.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(watchTower);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Add detailed monitoring of the house */
        player0.addDetailedMonitoring(watchTower);

        /* Verify that there is an event when promotions are disabled */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        watchTower.disablePromotions();

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {
                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(watchTower));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);
    }

    @Test
    public void testMonitorEventWhenProductionIsEnabled() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(sawmill);

        /* Wait for the sawmill to get populated */
        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Disable production */
        sawmill.stopProduction();

        /* Verify that no event comes when the production is enabled */
        sawmill.resumeProduction();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill));
        }

        /* Disable production */
        sawmill.stopProduction();

        /* Add detailed monitoring of the house */
        player0.addDetailedMonitoring(sawmill);

        /* Verify that an event comes when the production is enabled */
        GameChangesList lastGameChanges = monitor.getLastEvent();

        sawmill.resumeProduction();

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChanges)) {
            if (gameChangesList.getChangedBuildings().contains(sawmill)) {
                found++;
            }
        }

        assertEquals(found, 1);

        /* Disable production */
        sawmill.stopProduction();

        /* Remove detailed monitoring of the house */
        player0.removeDetailedMonitoring(sawmill);

        /* Verify that no event comes when the production is enabled */
        lastGameChanges = monitor.getLastEvent();

        sawmill.resumeProduction();

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChanges)) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill));
        }
    }

    @Test
    public void testMonitorEventWhenProductionIsDisabled() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(sawmill);

        /* Wait for the sawmill to get populated */
        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that no event comes when the production is disabled */
        sawmill.stopProduction();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill));
        }

        /* Enable production */
        sawmill.resumeProduction();

        /* Add detailed monitoring of the house */
        player0.addDetailedMonitoring(sawmill);

        /* Verify that an event comes when the production is disabled */
        GameChangesList lastGameChanges = monitor.getLastEvent();

        sawmill.stopProduction();

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChanges)) {
            if (gameChangesList.getChangedBuildings().contains(sawmill)) {
                found++;
            }
        }

        assertEquals(found, 1);

        /* Enable production */
        sawmill.resumeProduction();

        /* Remove detailed monitoring of the house */
        player0.removeDetailedMonitoring(sawmill);

        /* Verify that no event comes when the production is disabled */
        lastGameChanges = monitor.getLastEvent();

        sawmill.resumeProduction();

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChanges)) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill));
        }
    }

    @Test
    public void testMonitorEventWhenProductionBuildingReceivesMaterial() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all wood and sawmill workers from the headquarters */
        Utils.adjustInventoryTo(headquarter0, WOOD, 0);
        Utils.adjustInventoryTo(headquarter0, SAWMILL_WORKER, 0);

        /* Place sawmill, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(sawmill0);

        /* Place one piece of wood in the headquarters */
        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that no event is sent when the first wood is delivered */
        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 1);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill0));
        }

        /* Start detailed monitoring of the sawmill */
        player0.addDetailedMonitoring(sawmill0);

        /* Verify that an event is sent when the second piece of wood is delivered */
        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        monitor.clearEvents();

        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 2);

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(sawmill0)) {
                found++;
            }
        }

        assertEquals(found, 1);

        /* Verify that no event is sent when the third piece of wood is delivered */
        player0.removeDetailedMonitoring(sawmill0);

        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        monitor.clearEvents();

        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 3);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill0));
        }
    }

    @Test
    public void testMonitorEventWhenProductionBuildingConsumesMaterial() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all wood from the headquarters */
        Utils.adjustInventoryTo(headquarter0, WOOD, 0);

        /* Place sawmill, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        Sawmill sawmill0 = map.placeBuilding(new Sawmill(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, sawmill0.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(sawmill0);

        /* Place one piece of wood in the headquarters */
        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that no event is sent when the first wood is consumed */
        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 1);

        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmill0.getWorker(), PLANK);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill0));
        }

        /* Wait until the worker stops carrying the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, sawmill0.getWorker());

        /* Wait for the sawmill to get a second piece of wood */
        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        assertEquals(sawmill0.getAmount(WOOD), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 1);

        /* Start detailed monitoring of the sawmill */
        player0.addDetailedMonitoring(sawmill0);

        /* Verify that an event is sent when the second wood is consumed */
        monitor.clearEvents();

        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmill0.getWorker(), PLANK);

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(sawmill0)) {
                found++;
            }
        }

        assertEquals(found, 1);

        /* Stop detailed monitoring */
        player0.removeDetailedMonitoring(sawmill0);

        /* Wait for the productivity of the sawmill to reach 100 */
        for (int i = 0; i < 10000; i++) {
            if (sawmill0.getAmount(WOOD) == 0) {
                Utils.deliverCargo(sawmill0, WOOD);
            }

            if (sawmill0.getProductivity() == 100) {
                break;
            }

            map.stepTime();
        }

        assertEquals(sawmill0.getProductivity(), 100);

        /* Verify that no event is sent when the third wood is consumed */
        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 1);

        monitor.clearEvents();

        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmill0.getWorker(), PLANK);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill0));
        }
    }

    @Test
    public void testMonitorEventWhenSoldierEntersBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all soldiers from the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Place guard house, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        WatchTower watchTower = map.placeBuilding(new WatchTower(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, watchTower.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(watchTower);

        /* Let one soldier enter the building to make it occupied */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);

        Military military0 = Utils.waitForWorkerOutsideBuilding(Military.class, player0);

        Utils.fastForwardUntilWorkerReachesPoint(map, military0, watchTower.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that there is no event when the second soldier enters the house */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);

        Military military1 = Utils.waitForWorkerOutsideBuilding(Military.class, player0);

        Utils.fastForwardUntilWorkerReachesPoint(map, military1, watchTower.getPosition());

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertFalse(gameChangesList.getChangedBuildings().contains(watchTower));
        }

        /* Add detailed monitoring of the house */
        player0.addDetailedMonitoring(watchTower);

        /* Verify that there is an event when the third soldier enters the house */
        lastGameChangesList = monitor.getLastEvent();

        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);

        Military military2 = Utils.waitForWorkerOutsideBuilding(Military.class, player0);

        Utils.fastForwardUntilWorkerReachesPoint(map, military2, watchTower.getPosition());

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            if (!gameChangesList.getChangedBuildings().isEmpty()) {
                found++;

                assertTrue(gameChangesList.getChangedBuildings().contains(watchTower));
                assertEquals(gameChangesList.getChangedBuildings().size(), 1);
            }
        }

        assertEquals(found, 1);

        /* Remove detailed monitoring of the house */
        player0.removeDetailedMonitoring(watchTower);

        /* Verify that there is no event when the fourth soldier enters the house */
        lastGameChangesList = monitor.getLastEvent();

        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);

        Military military3 = Utils.waitForWorkerOutsideBuilding(Military.class, player0);

        Utils.fastForwardUntilWorkerReachesPoint(map, military3, watchTower.getPosition());

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertFalse(gameChangesList.getChangedBuildings().contains(watchTower));
        }
    }

    @Test
    public void testMonitorEventWhenSoldierLeavesBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // TODO: set up attack to make soldiers leave a military building
    }

    @Test
    public void testMonitorEventWhenProductivityChangedInBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, quarry.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(quarry);

        /* Wait for the quarry to get populated */
        Utils.waitForNonMilitaryBuildingToGetPopulated(quarry);

        /* Place stones close to the quarry */
        Point point2 = new Point(10, 6);
        map.placeStone(point2, StoneType.STONE_1, 10);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that no event is sent when the first stone is picked */
        Utils.fastForwardUntilWorkerCarriesCargo(map, quarry.getWorker(), STONE);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(quarry));
        }

        /* Wait until the worker stops carrying the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, quarry.getWorker());

        /* Verify that an event is sent when the productivity of the quarry changes */
        monitor.clearEvents();

        int originalProductivity = quarry.getProductivity();

        for (int i = 0; i < 10000; i++) {
            if (quarry.getProductivity() != originalProductivity) {
                break;
            }

            map.stepTime();
        }

        assertNotEquals(originalProductivity, quarry.getProductivity());

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(quarry)) {
                found++;
            }
        }

        assertEquals(found, 1);
    }

    @Test
    public void testMonitorEventWhenUpgradingMilitaryBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place guard house, connect it to the headquarters, and construct it */
        Point point1 = new Point(9, 5);
        Barracks barracks = map.placeBuilding(new Barracks(player0), point1);

        Road road0 = map.placeAutoSelectedRoad(player0, barracks.getFlag(), headquarter0.getFlag());

        Utils.constructHouse(barracks);

        /* Put a lot of planks and stones in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 20);
        Utils.adjustInventoryTo(headquarter0, STONE, 20);

        /* Remove all soldiers from the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter0, PRIVATE_FIRST_CLASS, 0);
        Utils.adjustInventoryTo(headquarter0, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter0, OFFICER, 0);
        Utils.adjustInventoryTo(headquarter0, GENERAL, 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that no event is sent when the barracks is upgraded */
        barracks.upgrade();

        Utils.waitForUpgradeToFinish(barracks);

        Building guardHouse = map.getBuildingAtPoint(point1);

        /* Start detailed monitoring */
        player0.addDetailedMonitoring(guardHouse);

        /* Verify that an event is sent when the guard house is requested to start upgrading */
        monitor.clearEvents();

        guardHouse.upgrade();

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(guardHouse)) {
                found++;
            }
        }

        assertEquals(found, 1);

        /* Verify that an event is sent when the upgrade is done */
        monitor.clearEvents();

        Utils.waitForUpgradeToFinish(guardHouse);

        Building watchTower = map.getBuildingAtPoint(point1);

        found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(watchTower)) {
                found++;
            }
        }

        assertEquals(found, 1);

        /* Stop detailed monitoring */
        player0.removeDetailedMonitoring(watchTower);

        /* Verify that no events are sent for the next upgrade */
        monitor.clearEvents();

        watchTower.upgrade();

        Utils.waitForUpgradeToFinish(watchTower);

        Building fortress = map.getBuildingAtPoint(point1);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(watchTower));
        }
    }


    @Test
    public void testMonitorEventWhenChangingAmountReservedInHeadquarters() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all soldiers from the headquarters */
        Utils.removeAllSoldiersFromStorage(headquarter0);

        /* Start monitoring */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that changing reserved soldiers does not cause an event to be sent */
        assertNotEquals(headquarter0.getReservedSoldiers(Military.Rank.PRIVATE_RANK), 1);

        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 1);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(headquarter0));
        }

        /* Add detailed monitoring */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that changing reserved soldiers now causes an event to be sent */
        monitor.clearEvents();

        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 2);

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(headquarter0)) {
                found++;
            }
        }

        assertEquals(found, 1);

        /* Remove detailed monitoring */
        player0.removeDetailedMonitoring(headquarter0);

        /* Verify that changing reserved soldiers doesn't cause an event to be sent */
        monitor.clearEvents();

        headquarter0.setReservedSoldiers(Military.Rank.PRIVATE_RANK, 3);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(headquarter0));
        }
    }

    // TODO: test detailed monitoring continues when a building is upgraded (and thus replaced)
    // TODO: monitor changes in ability to attack building
}
