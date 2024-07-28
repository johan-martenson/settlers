package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.GuardHouse;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.WatchTower;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.appland.settlers.model.Material.*;
import static org.junit.Assert.*;

public class TestGameMonitoringOfBuilding {

    @Test
    public void testMonitoringEventWhenBeerIsAddedToHeadquarter() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        /* Let the courier start walking from the headquarters' flag to it */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        map.stepTime();

        monitor.clearEvents();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that a house updated event is sent when the headquarters receives cargo */
        Cargo beerCargo1 = Utils.placeCargo(map, BEER, flag0, headquarter0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, beerCargo1);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        map.stepTime();

        monitor.clearEvents();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        map.stepTime();

        assertEquals(
                monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(headquarter0))
                        .count(),
                1);

        /* Turn off detailed monitoring */
        player0.removeDetailedMonitoring(headquarter0);

        /* Verify that no house updated event is sent when the headquarters receives cargo */
        Cargo beerCargo2 = Utils.placeCargo(map, BEER, flag0, headquarter0);

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, beerCargo2);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        map.stepTime();

        monitor.clearEvents();

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertEquals(
                monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(headquarter0))
                        .count(),
                0);

    }

    @Test
    public void testMonitoringEventWhenPlankIsRemovedFromHeadquarter() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        /* Place farm */
        Point point2 = new Point(9, 5);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Wait for the road to get assigned a courier */
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that a house updated event is sent when the plank leaves the headquarters */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, courier);

        for (int i = 0; i < 2000; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == PLANK) {
                break;
            }

            monitor.clearEvents();

            map.stepTime();
        }

        assertTrue(monitor.getEvents().stream()
                .mapToInt(gcl -> gcl.getChangedBuildings().contains(headquarter0) ? 1 : 0)
                .sum() > 0);
    }

    @Test
    public void testMonitoringEventWhenPlankIsAddedToBuildingUnderConstruction() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        monitor.clearEvents();

        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, PLANK);

        for (int i = 0; i < 1000; i++) {
            if (courier.getPosition().equals(farm0.getPosition())) {
                break;
            } else {
                monitor.clearEvents();
            }

            map.stepTime();
        }

        assertEquals(
                monitor.getEvents()
                        .stream()
                        .filter(gameChangesList -> gameChangesList.getChangedBuildings().contains(farm0)).count(),
                1
        );
    }

    @Test
    public void testMonitoringEventWhenConstructionProgresses() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point2 = new Point(9, 5);
        Farm farm0 = map.placeBuilding(new Farm(player0), point2);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), farm0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that events are sent when enough progress has been made (but not otherwise) */
        int trackedProgress = 0;

        for (int i = 0; i < 5000; i++) {
            if (farm0.getConstructionProgress() == 100) {
                break;
            }

            if (farm0.getConstructionProgress() != trackedProgress) {
                trackedProgress = farm0.getConstructionProgress();

                if (farm0.getConstructionProgress() % 10 == 0) {
                    assertTrue(monitor.getLastEvent().getChangedBuildings().contains(farm0));

                    monitor.clearEvents();
                }
            }

            map.stepTime();
        }
    }

    @Test
    public void testMonitoringEventWhenReservedLimitIsRaisedAndSoldierInInventoryBecomesHosted() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Set the reserved soldiers for the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* No house updated event is sent when the reserve limit is raised the first time */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 1);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 1);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 1);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 1);
        assertEquals(headquarter0.getAmount(PRIVATE), 9);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(headquarter0));
        }

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that a house updated event is sent when the reserve limit is raised the second time */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 3);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 3);
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

        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 5);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 5);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 5);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 5);
        assertEquals(headquarter0.getAmount(PRIVATE), 5);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventWhenReservedLimitIsLoweredAndHostedSoldierMovesToInventory() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Adjust resources in the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 10);

        /* Set the reserved soldiers for the headquarters */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 10);
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_FIRST_CLASS_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.SERGEANT_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.OFFICER_RANK, 0);
        headquarter0.setReservedSoldiers(Soldier.Rank.GENERAL_RANK, 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* No house updated event is sent when the reserve limit is raised the first time */
        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 5);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 5);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 5);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 5);
        assertEquals(headquarter0.getAmount(PRIVATE), 5);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(headquarter0));
        }

        /* Request detailed monitoring of the headquarters */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that a house updated event is sent when the reserve limit is raised the second time */
        GameChangesList lastGameChangesList = monitor.getLastEvent();

        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 3);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 3);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 3);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 3);
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

        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 1);

        assertEquals(headquarter0.getReservedSoldiers(Soldier.Rank.PRIVATE_RANK), 1);
        //assertEquals(headquarter0.getHostedSoldiersWithRank(Soldier.Rank.PRIVATE_RANK), 1);
        assertEquals(headquarter0.getActualReservedSoldiers().get(Soldier.Rank.PRIVATE_RANK), (Integer) 1);
        assertEquals(headquarter0.getAmount(PRIVATE), 9);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastGameChangesList)) {
            assertTrue(gameChangesList.getChangedBuildings().isEmpty());
        }
    }

    @Test
    public void testMonitorEventWhenEvacuatingMilitaryBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        Soldier military0 = Utils.waitForWorkerOutsideBuilding(Soldier.class, player0);

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        /* Wait for the sawmill to receive the wood */
        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Stop production in the sawmill */
        sawmill0.stopProduction();

        assertTrue(sawmill0.needsMaterial(WOOD));

        /* Start detailed monitoring of the sawmill */
        player0.addDetailedMonitoring(sawmill0);

        /* Verify that an event is sent when the second piece of wood is delivered */
        assertEquals(sawmill0.getAmount(WOOD), 1);

        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier(), WOOD);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), sawmill0.getFlag().getPosition());

        map.stepTime();

        assertNotNull(road0.getCourier().getCargo());
        assertEquals(road0.getCourier().getCargo().getMaterial(), WOOD);
        assertEquals(road0.getCourier().getPosition(), sawmill0.getFlag().getPosition());
        assertEquals(road0.getCourier().getTarget(), sawmill0.getPosition());

        map.stepTime();

        monitor.clearEvents();

        System.out.println(road0.getCourier());

        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 2);

        assertEquals(sawmill0.getAmount(WOOD), 2);

        assertEquals(monitor.getEvents()
                        .stream()
                        .filter(gameChangesList -> gameChangesList.getChangedBuildings().contains(sawmill0))
                        .count(),
                1);

        /* Verify that no event is sent when the third piece of wood is delivered */
        player0.removeDetailedMonitoring(sawmill0);

        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier(), WOOD);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), sawmill0.getFlag().getPosition());

        map.stepTime();

        monitor.clearEvents();

        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 3);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(sawmill0));
        }
    }

    @Test
    public void testMonitorEventWhenProductionBuildingConsumesMaterial() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        /* Wait for the sawmill to get occupied */
        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill0);

        /* Wait until the worker stops carrying the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, sawmill0.getWorker());

        /* Wait for the sawmill to get a second piece of wood */
        Utils.adjustInventoryTo(headquarter0, WOOD, 1);

        assertEquals(sawmill0.getAmount(WOOD), 0);

        Utils.waitForBuildingToGetAmountOfMaterial(sawmill0, WOOD, 1);

        /* Wait for the door to close again */
        assertFalse(sawmill0.isDoorClosed());

        Utils.waitForDoorToClose(sawmill0);

        map.stepTime();

        /* Start detailed monitoring of the sawmill */
        player0.addDetailedMonitoring(sawmill0);

        /* Verify that an event is sent when the second wood is consumed */
        monitor.clearEvents();

        Utils.fastForwardUntilWorkerCarriesCargo(map, sawmill0.getWorker(), PLANK);

        var count = monitor.getEvents().stream()
                .filter(gcl -> gcl.getChangedBuildings().contains(sawmill0))
                .count();

        assertTrue(count > 0);

        /* Verify that a second event is not sent (before the door is closed again...) */
        map.stepTime();

        assertEquals(count, monitor.getEvents().stream()
                .filter(gcl -> gcl.getChangedBuildings().contains(sawmill0))
                .count(), count);
    }

    @Test
    public void testMonitorEventWhenSoldierEntersBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        Soldier military0 = Utils.waitForWorkerOutsideBuilding(Soldier.class, player0);

        Utils.fastForwardUntilWorkerReachesPoint(map, military0, watchTower.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Add detailed monitoring of the house */
        player0.addDetailedMonitoring(watchTower);

        /* Verify that there is an event when the third soldier enters the house */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);

        Soldier soldier = Utils.waitForWorkerOutsideBuilding(Soldier.class, player0);

        for (int i = 0; i < 2000; i++) {
            if (soldier.getPosition().equals(watchTower.getPosition())) {
                break;
            }

            monitor.clearEvents();

            map.stepTime();
        }

        assertEquals(
                monitor.getEvents().stream()
                        .mapToInt(gameChangesList -> gameChangesList.getChangedBuildings().contains(watchTower) ? 1 : 0)
                        .sum(), 1);
    }

    @Test
    public void testMonitorEventWhenSoldierLeavesBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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
        map.placeStone(point2, Stone.StoneType.STONE_1, 10);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait until the worker stops carrying the cargo */
        Utils.fastForwardUntilWorkerCarriesNoCargo(map, quarry.getWorker());

        /* Verify that an event is sent when the productivity of the quarry changes */
        monitor.clearEvents();

        int originalProductivity = quarry.getProductivity();

        for (int i = 0; i < 10000; i++) {
            if (quarry.getProductivity() != originalProductivity) {
                break;
            }

            monitor.clearEvents();

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
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        assertEquals(guardHouse.getClass(), GuardHouse.class);

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

            assertTrue(gameChangesList.getNewBuildings().isEmpty());
            assertTrue(gameChangesList.getRemovedBuildings().isEmpty());

            if (!gameChangesList.getUpgradedBuildings().isEmpty()) {
                Optional<GameChangesList.NewAndOldBuilding> newAndOldBuildingOptional = gameChangesList.getUpgradedBuildings().stream().findFirst();

                assertTrue(gameChangesList.getChangedBuildings().isEmpty());
                assertTrue(newAndOldBuildingOptional.isPresent());
                assertEquals(newAndOldBuildingOptional.get().oldBuilding, guardHouse);
                assertEquals(newAndOldBuildingOptional.get().newBuilding.getPosition(), guardHouse.getPosition());
                assertEquals(newAndOldBuildingOptional.get().newBuilding.getClass(), WatchTower.class);
                assertEquals(newAndOldBuildingOptional.get().newBuilding, watchTower);

                found++;
            }
        }

        assertEquals(found, 1);

        /* Stop detailed monitoring */
        player0.removeDetailedMonitoring(watchTower);

        /* Verify that there is still an event sent for the next upgrade */
        monitor.clearEvents();

        watchTower.upgrade();

        Utils.waitForUpgradeToFinish(watchTower);

        Building fortress = map.getBuildingAtPoint(point1);


        found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertTrue(gameChangesList.getNewBuildings().isEmpty());
            assertTrue(gameChangesList.getRemovedBuildings().isEmpty());

            if (!gameChangesList.getUpgradedBuildings().isEmpty()) {
                Optional<GameChangesList.NewAndOldBuilding> newAndOldBuildingOptional = gameChangesList.getUpgradedBuildings().stream().findFirst();

                assertTrue(newAndOldBuildingOptional.isPresent());
                assertEquals(newAndOldBuildingOptional.get().oldBuilding, watchTower);

                assertEquals(newAndOldBuildingOptional.get().newBuilding.getPosition(), watchTower.getPosition());
                assertEquals(newAndOldBuildingOptional.get().newBuilding.getClass(), Fortress.class);
                assertEquals(newAndOldBuildingOptional.get().newBuilding, fortress);

                found++;
            }
        }

        assertEquals(found, 1);
    }


    @Test
    public void testMonitorEventWhenChangingAmountReservedInHeadquarters() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
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

        /* Add detailed monitoring */
        player0.addDetailedMonitoring(headquarter0);

        /* Verify that changing reserved soldiers now causes an event to be sent */
        monitor.clearEvents();

        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 2);

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

        headquarter0.setReservedSoldiers(Soldier.Rank.PRIVATE_RANK, 3);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(headquarter0));
        }
    }

    // TODO: test detailed monitoring continues when a building is upgraded (and thus replaced)
    // TODO: monitor changes in ability to attack building - add soldier, remove soldier, upgrade, tear down

    @Test
    public void testAttackCapabilityIncreasesWhenSoldierEntersBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarters for the second player */
        Point point1 = new Point(29, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Remove all soldiers from player 0's headquarters */
        Utils.removeAllSoldiersFromStorage(headquarter0);

        /* Place barracks for player 0, connect it to the headquarters, and wait until it's constructed */
        Point point2 = new Point(15, 5);
        WatchTower watchTower = map.placeBuilding(new WatchTower(player0), point2);

        Road road0 = map.placeAutoSelectedRoad(player0, watchTower.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(watchTower);

        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 0);

        /* Start monitoring, and add detailed monitoring for player 0 of player 1's headquarters */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        player0.addDetailedMonitoring(headquarter1);

        /* Verify that an event is sent when a soldier enters the watch tower and the available attackers increases */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 2);

        monitor.clearEvents();

        Utils.waitForMilitaryBuildingToGetPopulated(watchTower, 2);

        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 1);

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(headquarter1)) {
                found++;
            }
        }

        assertEquals(found, 1);

        /* Stop detailed monitoring */
        player0.removeDetailedMonitoring(headquarter1);

        /* Verify that no event is sent when another soldier enters the watch tower and the available attackers increases */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 1);

        monitor.clearEvents();

        Utils.waitForMilitaryBuildingToGetPopulated(watchTower, 3);

        map.stepTime();

        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 2);

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertFalse(gameChangesList.getChangedBuildings().contains(headquarter1));
        }
    }

    @Test
    public void testAttackCapabilityDecreasesWhenSoldierLeavesBuilding() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarters for the second player */
        Point point1 = new Point(29, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Remove all soldiers from player 0's headquarters */
        Utils.removeAllSoldiersFromStorage(headquarter0);

        /* Place barracks for player 0, connect it to the headquarters, and wait until it's constructed */
        Point point2 = new Point(15, 5);
        WatchTower watchTower = map.placeBuilding(new WatchTower(player0), point2);

        Road road0 = map.placeAutoSelectedRoad(player0, watchTower.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(watchTower);

        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 0);

        /* Start monitoring */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for two soldiers to enter the watch tower */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 2);

        Utils.waitForMilitaryBuildingToGetPopulated(watchTower, 2);

        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 1);

        /* Add detailed monitoring for player 0 of player 1's headquarters */
        player0.addDetailedMonitoring(headquarter1);

        /* Verify that an event is sent when the soldiers leave the watch tower and the available attackers decrease */
        monitor.clearEvents();

        watchTower.evacuate();

        Utils.waitForWorkersOutsideBuilding(Soldier.class, 2, player0);

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(headquarter1)) {
                found++;
            }
        }

        assertEquals(found, 1);
    }

    @Test
    public void testAttackCapabilityIncreasesWhenBuildingIsUpgradedAndRangeIsExtended() {

    }

    @Test
    public void testAttackCapabilityDecreasesWhenMilitaryBuildingIsTornDown() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarters for the second player */
        Point point1 = new Point(29, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Remove all soldiers from player 0's headquarters */
        Utils.removeAllSoldiersFromStorage(headquarter0);

        /* Place barracks for player 0, connect it to the headquarters, and wait until it's constructed */
        Point point2 = new Point(15, 5);
        WatchTower watchTower = map.placeBuilding(new WatchTower(player0), point2);

        Road road0 = map.placeAutoSelectedRoad(player0, watchTower.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(watchTower);

        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 0);

        /* Start monitoring */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for two soldiers to enter the watch tower */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 2);

        Utils.waitForMilitaryBuildingToGetPopulated(watchTower, 2);

        assertEquals(player0.getAvailableAttackersForBuilding(headquarter1), 1);

        /* Add detailed monitoring for player 0 of player 1's headquarters */
        player0.addDetailedMonitoring(headquarter1);

        /* Verify that an event is sent when the soldiers leave the watch tower and the available attackers decrease */
        monitor.clearEvents();

        watchTower.tearDown();

        Utils.waitForWorkersOutsideBuilding(Soldier.class, 2, player0);

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getChangedBuildings().contains(headquarter1)) {
                found++;
            }
        }

        assertEquals(found, 1);
    }

    // TODO: test when soldiers leave for other reason - i.e. going out to attack or defend, changing military settings


    @Test
    public void testMonitoringEventWhenDoorOpensAndCloses() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place a woodcutter hut and connect it to the headquarters */
        var point1 = new Point(9, 7);
        var woodcutterHut = map.placeBuilding(new Woodcutter(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, woodcutterHut.getFlag(), headquarter0.getFlag());

        /* Place tree */
        var point2 = new Point(13, 9);
        var tree = map.placeTree(point2, Tree.TreeType.OAK, Tree.TreeSize.FULL_GROWN);

        /* Start monitoring */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that an event is sent when a woodcutter worker enters the woodcutter hut */

        /* Wait for the woodcutter worker to appear */
        for (int i = 0; i < 2000; i++) {
            if (!Utils.findWorkersOfTypeOutsideForPlayer(WoodcutterWorker.class, player0).isEmpty()) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        var worker = Utils.findWorkersOfTypeOutsideForPlayer(WoodcutterWorker.class, player0).getFirst();

        monitor.clearEvents();

        /* Wait for the woodcutter worker to get to the flag of the woodcutter hut */
        for (int i = 0; i < 2000; i++) {
            if (worker.isExactlyAtPoint() && Objects.equals(worker.getPosition(), woodcutterHut.getFlag().getPosition())) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());
            assertTrue(monitor.getEvents().stream().noneMatch(gcl -> gcl.getChangedBuildings().contains(woodcutterHut)));

            map.stepTime();
        }

        assertFalse(woodcutterHut.isDoorClosed());
        assertEquals(monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(woodcutterHut))
                        .count(),
                1);

        map.stepTime();

        assertEquals(monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(woodcutterHut))
                        .count(),
                1);

        /* Verify that an event is sent when the door closes again */
        monitor.clearEvents();

        /* Wait for the door to close */
        for (int i = 0; i < 2000; i++) {
            if (woodcutterHut.isDoorClosed()) {
                break;
            }

            assertFalse(monitor.getEvents().stream().anyMatch(gcl -> gcl.getChangedBuildings().contains(woodcutterHut)));

            map.stepTime();
        }

        assertTrue(woodcutterHut.isDoorClosed());
        assertEquals(monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(woodcutterHut))
                        .count(),
                1);

        map.stepTime();

        assertTrue(woodcutterHut.isDoorClosed());
        assertEquals(monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(woodcutterHut))
                        .count(),
                1);

        /* Verify that an event is sent when the woodcutter leaves the woodcutter hut */
        monitor.clearEvents();

        /* Wait for the woodcutter to leave the hut */
        assertTrue(woodcutterHut.getWorker().isInsideBuilding());

        for (int i = 0; i < 2000; i++) {
            if (!woodcutterHut.getWorker().isInsideBuilding()) {
                break;
            }

            assertTrue(woodcutterHut.isDoorClosed());

            map.stepTime();
        }

        assertFalse(woodcutterHut.getWorker().isInsideBuilding());
        assertFalse(woodcutterHut.isDoorClosed());
        assertEquals(monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(woodcutterHut))
                        .count(),
                1);

        map.stepTime();

        assertFalse(woodcutterHut.isDoorClosed());
        assertEquals(monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(woodcutterHut))
                        .count(),
                1);

        /* Verify that an event is sent when the door closes by itself */
        monitor.clearEvents();

        Utils.waitForDoorToClose(woodcutterHut);

        assertTrue(woodcutterHut.isDoorClosed());
        assertEquals(monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(woodcutterHut))
                        .count(),
                1);

        map.stepTime();

        assertTrue(woodcutterHut.isDoorClosed());
        assertEquals(monitor.getEvents().stream()
                        .filter(gcl -> gcl.getChangedBuildings().contains(woodcutterHut))
                        .count(),
                1);
    }

    @Test
    public void testEventWhenHeadquartersReceivesCargo() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all planks from the headquarters */
        Utils.clearInventory(headquarter0, PLANK);

        /* Place second flag and connect it to the headquarters */
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Wait for the road to get assigned a courier */
        var courier = Utils.waitForRoadToGetAssignedCourier(map, road0);

        /* Place a cargo on the flag intended for the headquarters */
        var cargo = Utils.placeCargo(map, PLANK, flag0, headquarter0);

        /* Wait for the courier to carry the cargo */
        Utils.fastForwardUntilWorkerCarriesCargo(map, courier, cargo);

        /* Wait for the courier to get close to the headquarters but not yet deliver the cargo */
        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getFlag().getPosition());

        Utils.fastForward(9, map);

        assertEquals(courier.getTarget(), headquarter0.getPosition());
        assertTrue(courier.isTraveling());
        assertFalse(courier.isExactlyAtPoint());
        assertEquals(courier.getNextPoint(), headquarter0.getPosition());

        /* Start monitoring and add detailed monitoring of the headquarters */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        player0.addDetailedMonitoring(headquarter0);

        monitor.clearEvents();

        /* Verify that an event is sent when the headquarters receives the delivery */
        assertEquals(headquarter0.getAmount(PLANK), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        assertEquals(headquarter0.getAmount(PLANK), 1);
        assertNotEquals(monitor.getEvents().size(), 0);
        assertTrue(monitor.getLastEvent().getChangedBuildings().contains(headquarter0));
    }

    @Test
    public void testEventWhenHeadquartersReceivesWorker() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all stonemasons from the headquarters */
        Utils.clearInventory(headquarter0, STONEMASON);

        /* Place second flag and connect it to the headquarters */
        var point1 = new Point(10, 4);
        var flag0 = map.placeFlag(player0, point1);

        var road0 = map.placeAutoSelectedRoad(player0, flag0, headquarter0.getFlag());

        /* Place a stonemason on the flag and make it go to the headquarters */
        var stonemason = new Stonemason(player0, map);
        map.placeWorker(stonemason, flag0);
        stonemason.setPosition(flag0.getPosition());
        stonemason.returnToStorage();

        /* Wait for the stonemason to get close to the headquarters but not yet reach it */
        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, headquarter0.getFlag().getPosition());

        Utils.fastForward(9, map);

        assertEquals(stonemason.getTarget(), headquarter0.getPosition());
        assertTrue(stonemason.isTraveling());
        assertFalse(stonemason.isExactlyAtPoint());
        assertEquals(stonemason.getNextPoint(), headquarter0.getPosition());

        /* Start monitoring and add detailed monitoring of the headquarters */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        player0.addDetailedMonitoring(headquarter0);

        monitor.clearEvents();

        /* Verify that an event is sent when the headquarters receives the delivery */
        assertEquals(headquarter0.getAmount(STONEMASON), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, stonemason, headquarter0.getPosition());

        assertEquals(headquarter0.getAmount(STONEMASON), 1);
        assertNotEquals(monitor.getEvents().size(), 0);
        assertTrue(monitor.getLastEvent().getChangedBuildings().contains(headquarter0));
    }

    @Test
    public void testEventWhenSoldierLeavesHeadquarters() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Remove all soldiers from the headquarters */
        Utils.removeAllSoldiersFromStorage(headquarter0);

        /* Place a fortress and connect it to the headquarters */
        var point1 = new Point(10, 4);
        var fortress = map.placeBuilding(new Fortress(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, fortress.getFlag(), headquarter0.getFlag());

        /* Wait for the fortress to get constructed */
        Utils.waitForBuildingToBeConstructed(fortress);

        /* Start monitoring and add detailed monitoring of the headquarters */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        player0.addDetailedMonitoring(headquarter0);

        /* Add soldiers to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PRIVATE, 20);

        assertEquals(map.getWorkers().stream()
                .filter(Worker::isSoldier)
                .count(), 0);

        /* Verify that an event is sent each time a soldier leaves the headquarters to go to the fortress */
        for (int i = 0; i < 9; i++) {
            monitor.clearEvents();

            for (int j = 0; j < 2000; j++) {
                if (map.getWorkers().stream()
                        .filter(Worker::isSoldier)
                        .count() == i + 1) {
                    break;
                }

                monitor.clearEvents();

                map.stepTime();
            }

            assertEquals(map.getWorkers().stream()
                    .filter(Worker::isSoldier)
                    .count(), i + 1);
            assertTrue(monitor.getLastEvent().getChangedBuildings().contains(headquarter0));
        }
    }

    @Test
    public void testEventWhenWorkerLeavesHeadquarters() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter for the first player */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Control the amount of builders in the headquarters */
        Utils.adjustInventoryTo(headquarter0, BUILDER, 2);

        /* Place a building and connect it to the headquarters */
        var point1 = new Point(5, 9);
        var woodcutterHut = map.placeBuilding(new Woodcutter(player0), point1);

        var road0 = map.placeAutoSelectedRoad(player0, woodcutterHut.getFlag(), headquarter0.getFlag());

        /* Start monitoring and add detailed monitoring of the headquarters */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        player0.addDetailedMonitoring(headquarter0);

        /* Verify that an event is sent when the builder goes out to construct the building */
        assertEquals(monitor.getEvents().size(), 0);

        Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        assertTrue(monitor.getLastEvent().getChangedBuildings().contains(headquarter0));
    }
}