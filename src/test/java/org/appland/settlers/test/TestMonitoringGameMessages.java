package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Miner;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.messages.BuildingCapturedMessage;
import org.appland.settlers.model.messages.BuildingLostMessage;
import org.appland.settlers.model.messages.GeologistFindMessage;
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.model.messages.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.messages.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.messages.NoMoreResourcesMessage;
import org.appland.settlers.model.messages.StoreHouseIsReadyMessage;
import org.appland.settlers.model.messages.TreeConservationProgramActivatedMessage;
import org.appland.settlers.model.messages.TreeConservationProgramDeactivatedMessage;
import org.appland.settlers.model.messages.UnderAttackMessage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Vegetation.WATER;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.messages.Message.MessageType.*;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

public class TestMonitoringGameMessages {

    /**
     * TODO:
     *  - Test titles
     *  - Only sent once
     *  - To the right player
     *  - Test all messages received before starting to monitor are not sent to the monitor
     *  - Test new message types:
     *    - Bombarded by catapult
     *    - Game ended
     *    - This building has caused you to lose land
     */

    @Test
    public void testNoMessagesOnStart() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that there are no event for messages on start */
        for (GameChangesList changes : monitor.getEvents()) {
            assertEquals(changes.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventForMessageIsReceivedWhenBarracksIsReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a message is sent when the barracks is finished */
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        MilitaryBuildingReadyMessage message = (MilitaryBuildingReadyMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), Message.MessageType.MILITARY_BUILDING_READY);
        assertEquals(message.building(), barracks0);
    }

    @Test
    public void testMonitoringEventWhenMessageIsRemoved() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* A message is sent when the barracks is finished */
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        MilitaryBuildingReadyMessage message = (MilitaryBuildingReadyMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), Message.MessageType.MILITARY_BUILDING_READY);
        assertEquals(message.building(), barracks0);

        /* Verify that an event is sent when the message is removed */
        monitor.clearEvents();

        player0.removeMessage(message);

        map.stepTime();

        int found = 0;
        for (GameChangesList gameChangesList1 : monitor.getEvents()) {
            if (!gameChangesList1.removedMessages().isEmpty()) {
                found++;

                assertTrue(gameChangesList1.removedMessages().contains(message));
                assertEquals(gameChangesList1.removedMessages().size(), 1);
            }
        }

        assertEquals(found, 1);

        /* Verify that the event is only sent once */
        monitor.clearEvents();

        map.stepTime();

        for (GameChangesList gameChangesList1 : monitor.getEvents()) {
            assertTrue(gameChangesList1.removedMessages().isEmpty());
        }
    }

    @Test
    public void testMonitoringEventForMessageIsReceivedWhenBarracksIsReadyIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a message is sent when the barracks is finished */
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        MilitaryBuildingReadyMessage message = (MilitaryBuildingReadyMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), Message.MessageType.MILITARY_BUILDING_READY);
        assertEquals(message.building(), barracks0);

        /* Verify that the event is only sent once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventForMessageIsReceivedWhenBarracksIsReadyBeforeMonitoringStarts() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 17);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Verify that a message is sent when the barracks is finished */
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMessageWhenNonMilitaryBuildingIsReady() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Connect the bakery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter0.getFlag());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Finish construction of the bakery */
        constructHouse(bakery);

        /* Verify that no message was sent */
        for (GameChangesList changesList : monitor.getEvents()) {
            assertEquals(changesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenBorderIsExtendedWhenBarracksIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a soldier to walk to the barracks */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Soldier.class);

        Soldier military = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Soldier) {
                military = (Soldier)worker;
            }
        }

        assertNotNull(military);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify a message is sent when the barracks is populated */
        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        MilitaryBuildingOccupiedMessage message = (MilitaryBuildingOccupiedMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), Message.MessageType.MILITARY_BUILDING_OCCUPIED);
        assertEquals(message.building(), barracks0);
    }

    @Test
    public void testMonitoringEventWhenBorderIsExtendedWhenBarracksIsPopulatedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a soldier to walk to the barracks */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Soldier.class);

        Soldier military = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Soldier) {
                military = (Soldier)worker;
            }
        }

        assertNotNull(military);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify a message is sent when the barracks is populated */
        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        MilitaryBuildingOccupiedMessage message = (MilitaryBuildingOccupiedMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), Message.MessageType.MILITARY_BUILDING_OCCUPIED);
        assertEquals(message.building(), barracks0);

        /* Verify that the event is only reported once */
        Utils.fastForward(10, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenBorderIsExtendedWhenBarracksIsPopulatedBeforeMonitoringStarts() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        /* Wait for a soldier to walk to the barracks */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Soldier.class);

        Soldier military = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Soldier) {
                military = (Soldier)worker;
            }
        }

        assertNotNull(military);

        /* Verify a message is sent when the barracks is populated */
        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenQuarryRunsOutOfResources() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason);

        /* Connect the quarry with the headquarters */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Put a new stone if the current one is gone */
        if (map.getStones().isEmpty()) {
            Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);
        }

        /* Very that a message is sent when the quarry takes all the stone */
        for (int i = 0; i < 5000; i++) {

            map.stepTime();

            if (map.getStones().isEmpty()) {
                break;
            }

            assertTrue(player0.getMessages().isEmpty());
        }

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Give the stonemason a chance to discover that there is no more stone */
        Utils.waitForNewMessage(player0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), NO_MORE_RESOURCES);

        assertEquals(message.building(), quarry0);
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenQuarryRunsOutOfResourcesIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason);

        /* Connect the quarry with the headquarters */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Put a new stone if the current one is gone */
        if (map.getStones().isEmpty()) {
            Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);
        }

        /* Very that a message is sent when the quarry takes all the stone */
        for (int i = 0; i < 5000; i++) {

            map.stepTime();

            if (map.getStones().isEmpty()) {
                break;
            }

            assertTrue(player0.getMessages().isEmpty());
        }

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Give the stonemason a chance to discover that there is no more stone */
        Utils.waitForNewMessage(player0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), NO_MORE_RESOURCES);

        assertEquals(message.building(), quarry0);

        /* Verify that the event is only sent once */
        Utils.fastForward(200, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessageSentWhenQuarryRunsOutOfResourcesBeforeMonitoringStarts() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        constructHouse(quarry0);

        /* Populate the quarry */
        Worker stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0);

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason);

        /* Connect the quarry with the headquarters */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), quarry0.getFlag());

        /* Put a new stone if the current one is gone */
        if (map.getStones().isEmpty()) {
            Utils.putStoneOnOnePoint(map.getPointsWithinRadius(quarry0.getPosition(), 4), map);
        }

        /* Very that a message is sent when the quarry takes all the stone */
        for (int i = 0; i < 5000; i++) {

            map.stepTime();

            if (map.getStones().isEmpty()) {
                break;
            }

            assertTrue(player0.getMessages().isEmpty());
        }

        /* Give the stonemason a chance to discover that there is no more stone */
        Utils.waitForNewMessage(player0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenFishermanCanRunOutOfFish() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Remove fishes until there is only one left */
        Utils.removeAllFish(map, point1);
        Utils.removeAllFish(map, point2);

        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point0) == 1) {
                break;
            }

            map.catchFishAtPoint(point0);
        }

        /* Place headquarters */
        Point point3 = new Point(15, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Place a road from the headquarters to the fishery */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), fishery.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Let the fisherman reach the spot and start fishing */
        int amountOfFish = map.getAmountFishAtPoint(point);

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Wait for the fisherman to finish fishing */
        assertFalse(fishery.isOutOfNaturalResources());

        Utils.waitForFishermanToStopFishing(fisherman, map);

        /* Let the fisherman go back to the fishery */
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        /* Wait for the fisherman to leave the cargo of fish at the flag */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

        assertNull(fisherman.getCargo());
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        /* Verify that a message is sent when there is no more fish */
        assertEquals(map.getAmountFishAtPoint(point0), 0);
        assertEquals(map.getAmountFishAtPoint(point1), 0);
        assertEquals(map.getAmountFishAtPoint(point2), 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Give the fisherman some time to find out that there is no more fish */
        Utils.waitForNewMessage(player0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), NO_MORE_RESOURCES);
        assertEquals(message.building(), fishery);
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenFishermanCanRunOutOfFishIsOnlySentOnce() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Remove fishes until there is only one left */
        Utils.removeAllFish(map, point1);
        Utils.removeAllFish(map, point2);

        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point0) == 1) {
                break;
            }

            map.catchFishAtPoint(point0);
        }

        /* Place headquarters */
        Point point3 = new Point(15, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Place a road from the headquarters to the fishery */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), fishery.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Let the fisherman reach the spot and start fishing */
        int amountOfFish = map.getAmountFishAtPoint(point);

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Wait for the fisherman to finish fishing */
        assertFalse(fishery.isOutOfNaturalResources());

        Utils.waitForFishermanToStopFishing(fisherman, map);

        /* Let the fisherman go back to the fishery */
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        /* Wait for the fisherman to leave the cargo of fish at the flag */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

        assertNull(fisherman.getCargo());
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        /* Verify that a message is sent when there is no more fish */
        assertEquals(map.getAmountFishAtPoint(point0), 0);
        assertEquals(map.getAmountFishAtPoint(point1), 0);
        assertEquals(map.getAmountFishAtPoint(point2), 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Give the fisherman some time to find out that there is no more fish */
        Utils.waitForNewMessage(player0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), NO_MORE_RESOURCES);
        assertEquals(message.building(), fishery);

        /* Verify that the event is only sent once */
        Utils.fastForward(200, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessageSentWhenFishermanCanRunOutOfFishBeforeMonitoringStarts() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Remove fishes until there is only one left */
        Utils.removeAllFish(map, point1);
        Utils.removeAllFish(map, point2);

        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point0) == 1) {
                break;
            }

            map.catchFishAtPoint(point0);
        }

        /* Place headquarters */
        Point point3 = new Point(15, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Place a road from the headquarters to the fishery */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), fishery.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Let the fisherman reach the spot and start fishing */
        int amountOfFish = map.getAmountFishAtPoint(point);

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Wait for the fisherman to finish fishing */
        assertFalse(fishery.isOutOfNaturalResources());

        Utils.waitForFishermanToStopFishing(fisherman, map);

        /* Let the fisherman go back to the fishery */
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        /* Wait for the fisherman to leave the cargo of fish at the flag */
        map.stepTime();

        assertEquals(fisherman.getTarget(), fishery.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, fisherman, fishery.getFlag().getPosition());

        assertNull(fisherman.getCargo());
        assertEquals(fisherman.getTarget(), fishery.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        /* Verify that a message is sent when there is no more fish */
        assertEquals(map.getAmountFishAtPoint(point0), 0);
        assertEquals(map.getAmountFishAtPoint(point1), 0);
        assertEquals(map.getAmountFishAtPoint(point2), 0);

        /* Give the fisherman some time to find out that there is no more fish */
        Utils.waitForNewMessage(player0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenGeologistFindsGoldOnMountain() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a mountain with gold */
        Utils.createMinableMountainWithinRadius(point1, 9, map);
        Utils.putMineralWithinRadius(GOLD, point1, 9, map);

        /* Connect headquarters and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a message is sent when the geologist finds gold */
        assertTrue(geologist.isInvestigating());
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForward(20, map);

        assertTrue(map.isSignAtPoint(geologist.getPosition()));
        assertNotNull(map.getSignAtPoint(geologist.getPosition()));

        Sign sign = map.getSignAtPoint(geologist.getPosition());

        assertEquals(sign.getType(), GOLD);
        assertEquals(sign.getSize(), LARGE);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        GeologistFindMessage message = (GeologistFindMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), GEOLOGIST_FIND);
        assertEquals(message.point(), geologist.getPosition());
        assertEquals(message.material(), GOLD);
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenGeologistFindsGoldOnMountainIsSentOnlyOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a mountain with gold */
        Utils.createMinableMountainWithinRadius(point1, 9, map);
        Utils.putMineralWithinRadius(GOLD, point1, 9, map);

        /* Connect headquarters and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that a message is sent when the geologist finds gold */
        assertTrue(geologist.isInvestigating());
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForward(20, map);

        assertTrue(map.isSignAtPoint(geologist.getPosition()));
        assertNotNull(map.getSignAtPoint(geologist.getPosition()));

        Sign sign = map.getSignAtPoint(geologist.getPosition());

        assertEquals(sign.getType(), GOLD);
        assertEquals(sign.getSize(), LARGE);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        GeologistFindMessage message = (GeologistFindMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), GEOLOGIST_FIND);
        assertEquals(message.point(), geologist.getPosition());
        assertEquals(message.material(), GOLD);

        /* Verify that the event is only sent once */
        Utils.fastForward(5, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessageSentWhenGeologistFindsGoldOnMountainBeforeMonitoringStarts() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a mountain with gold */
        Utils.createMinableMountainWithinRadius(point1, 9, map);
        Utils.putMineralWithinRadius(GOLD, point1, 9, map);

        /* Connect headquarters and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Wait for the geologist to go to the flag */
        map.stepTime();

        Geologist geologist = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Wait for the geologist to reach the first site to investigate */
        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        /* Verify that a message is sent when the geologist finds gold */
        assertTrue(geologist.isInvestigating());
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForward(20, map);

        assertTrue(map.isSignAtPoint(geologist.getPosition()));
        assertNotNull(map.getSignAtPoint(geologist.getPosition()));

        Sign sign = map.getSignAtPoint(geologist.getPosition());

        assertEquals(sign.getType(), GOLD);
        assertEquals(sign.getSize(), LARGE);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenBarracksIsUnderAttack() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        constructHouse(barracks0);
        constructHouse(barracks1);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Verify that no soldiers leave the barracks before the attack is initiated */
        for (int i = 0; i < 100; i++) {
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Soldier) {
                    assertTrue(worker.isInsideBuilding());
                }
            }

            map.stepTime();
        }

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        map.stepTime();

        GameChangesList changesBeforeAttack = monitor.getLastEvent();

        if (changesBeforeAttack != null) {
            assertEquals(changesBeforeAttack.newMessages().size(), 0);
        }

        /* Verify that a message is sent to player 1 when it's attacked */
        assertTrue(player1.getMessages().size() >= 2);

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        map.stepTime();

        GameChangesList gameChangesList = monitor.getLastEvent();
        assertEquals(gameChangesList.newMessages().size(), 1);

        UnderAttackMessage message = (UnderAttackMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), UNDER_ATTACK);
        assertEquals(message.building(), barracks1);
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenBarracksIsUnderAttackIsSentOnlyOnce() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        constructHouse(barracks0);
        constructHouse(barracks1);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

        /* Verify that no soldiers leave the barracks before the attack is initiated */
        for (int i = 0; i < 100; i++) {
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Soldier) {
                    assertTrue(worker.isInsideBuilding());
                }
            }

            map.stepTime();
        }

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player1.monitorGameView(monitor);

        map.stepTime();

        GameChangesList changesBeforeAttack = monitor.getLastEvent();

        if (changesBeforeAttack != null) {
            assertEquals(changesBeforeAttack.newMessages().size(), 0);
        }

        /* Verify that a message is sent to player 1 when it's attacked */
        assertTrue(player1.getMessages().size() >= 2);

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        map.stepTime();

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        UnderAttackMessage message = (UnderAttackMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), UNDER_ATTACK);
        assertEquals(message.building(), barracks1);

        /* Verify that the event is only sent once */
        Utils.fastForward(200, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessageSentWhenBarracksIsUnderAttackBeforeMonitoringStarts() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        constructHouse(barracks0);
        constructHouse(barracks1);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Verify that no soldiers leave the barracks before the attack is initiated */
        for (int i = 0; i < 100; i++) {
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Soldier) {
                    assertTrue(worker.isInsideBuilding());
                }
            }

            map.stepTime();
        }

        /* Verify that a message is sent to player 1 when it's attacked */
        //assertEquals(player1.getMessages().size(), 2);
        assertTrue(player1.getMessages().size() >= 2);

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessagesAreSentWhenAnAttackerTakesOverBuildingAfterWinningFight() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear the soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        constructHouse(barracks0);
        constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitorForPlayer0 = new Utils.GameViewMonitor();
        Utils.GameViewMonitor monitorForPlayer1 = new Utils.GameViewMonitor();
        player0.monitorGameView(monitorForPlayer0);
        player1.monitorGameView(monitorForPlayer1);

        /* Verify that the attacker takes over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(player0.getMessages().size() >= 2);
        assertTrue(player1.getMessages().size() >= 3);

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        GameChangesList gameChangesListForPlayer0 = monitorForPlayer0.getLastEvent();
        GameChangesList gameChangesListForPlayer1 = monitorForPlayer1.getLastEvent();

        assertEquals(gameChangesListForPlayer0.newMessages().size(), 1);
        assertEquals(gameChangesListForPlayer1.newMessages().size(), 1);

        BuildingCapturedMessage messageForPlayer0 = (BuildingCapturedMessage) gameChangesListForPlayer0.newMessages().getFirst();
        BuildingLostMessage messageForPlayer1 = (BuildingLostMessage) gameChangesListForPlayer1.newMessages().getFirst();

        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(messageForPlayer0.getMessageType(), BUILDING_CAPTURED);

        assertEquals(messageForPlayer1.building(), barracks1);
        assertEquals(messageForPlayer1.getMessageType(), BUILDING_LOST);
        assertEquals(messageForPlayer1.building(), barracks1);
    }

    @Test
    public void testMonitoringEventWhenMessagesAreSentWhenAnAttackerTakesOverBuildingAfterWinningFightIsSentOnlyOnce() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear the soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        constructHouse(barracks0);
        constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitorForPlayer0 = new Utils.GameViewMonitor();
        Utils.GameViewMonitor monitorForPlayer1 = new Utils.GameViewMonitor();
        player0.monitorGameView(monitorForPlayer0);
        player1.monitorGameView(monitorForPlayer1);

        /* Verify that the attacker takes over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(player0.getMessages().size() >= 2);
        assertTrue(player1.getMessages().size() >= 3);

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        GameChangesList gameChangesListForPlayer0 = monitorForPlayer0.getLastEvent();
        GameChangesList gameChangesListForPlayer1 = monitorForPlayer1.getLastEvent();

        assertEquals(gameChangesListForPlayer0.newMessages().size(), 1);
        assertEquals(gameChangesListForPlayer1.newMessages().size(), 1);

        BuildingCapturedMessage messageForPlayer0 = (BuildingCapturedMessage) gameChangesListForPlayer0.newMessages().getFirst();
        BuildingLostMessage messageForPlayer1 = (BuildingLostMessage) gameChangesListForPlayer1.newMessages().getFirst();

        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(messageForPlayer0.getMessageType(), BUILDING_CAPTURED);

        assertEquals(messageForPlayer1.building(), barracks1);
        assertEquals(messageForPlayer1.getMessageType(), BUILDING_LOST);
        assertEquals(messageForPlayer1.building(), barracks1);

        /* Verify that the event is only sent once */
        Utils.fastForward(200, map);

        for (GameChangesList newChanges : monitorForPlayer0.getEventsAfterEvent(gameChangesListForPlayer0)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }

        for (GameChangesList newChanges : monitorForPlayer1.getEventsAfterEvent(gameChangesListForPlayer1)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessagesAreSentWhenAnAttackerTakesOverBuildingAfterWinningFightBeforeMonitoringStarts() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(37, 15);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Clear the soldiers from the inventories of both headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(21, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        constructHouse(barracks0);
        constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Set up monitoring subscription for the player */

        /* Verify that the attacker takes over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(player0.getMessages().size() >= 2);
        assertTrue(player1.getMessages().size() >= 3);

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitorForPlayer0 = new Utils.GameViewMonitor();
        Utils.GameViewMonitor monitorForPlayer1 = new Utils.GameViewMonitor();
        player0.monitorGameView(monitorForPlayer0);
        player1.monitorGameView(monitorForPlayer1);

        map.stepTime();

        for (GameChangesList gameChangesList : monitorForPlayer0.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }

        for (GameChangesList gameChangesList : monitorForPlayer1.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenCoalmineRunsOutOfCoal() throws Exception {

        /* Create game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, SMALL, map);

        /* Remove all gold but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(COAL, point0) > 1) {
                map.mineMineralAtPoint(COAL, point0);
            }
        }

        /* Place a headquarters */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a coal mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Place a road from headquarters to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food to the miner */
        Utils.deliverCargo(mine, BREAD);
        Utils.deliverCargo(mine, FISH);
        Utils.deliverCargo(mine, MEAT);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to mine gold */
        Utils.fastForward(50, map);

        assertFalse(mine.isOutOfNaturalResources());

        /* Wait for the miner to leave the gold at the flag */
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());

        assertNull(miner.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());

        assertTrue(miner.isInsideBuilding());
        assertEquals(player0.getMessages().size(), 0);

        /* Verify that the gold is gone and that the miner gets no gold */
        assertEquals(map.getAmountOfMineralAtPoint(COAL, point0), 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Give the miner time to find out that there are no more resources */
        Utils.waitForNewMessage(player0);

        /* Verify that a message is sent when the mine has run out of resources */
        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), NO_MORE_RESOURCES);
        assertEquals(message.building(), mine);
        assertTrue(mine.isOutOfNaturalResources());
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenCoalmineRunsOutOfCoalIsOnlySentOnce() throws Exception {

        /* Create game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, SMALL, map);

        /* Remove all gold but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(COAL, point0) > 1) {
                map.mineMineralAtPoint(COAL, point0);
            }
        }

        /* Place a headquarters */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Place a road from headquarters to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food to the miner */
        Utils.deliverCargo(mine, BREAD);
        Utils.deliverCargo(mine, FISH);
        Utils.deliverCargo(mine, MEAT);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to mine gold */
        Utils.fastForward(50, map);

        assertFalse(mine.isOutOfNaturalResources());

        /* Wait for the miner to leave the gold at the flag */
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());

        assertNull(miner.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());

        assertTrue(miner.isInsideBuilding());
        assertEquals(player0.getMessages().size(), 0);

        /* Verify that the gold is gone and that the miner gets no gold */
        assertEquals(map.getAmountOfMineralAtPoint(COAL, point0), 0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Give the miner time to find out that there are no more resources */
        Utils.waitForNewMessage(player0);

        /* Verify that a message is sent when the mine has run out of resources */
        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), NO_MORE_RESOURCES);
        assertEquals(message.building(), mine);
        assertTrue(mine.isOutOfNaturalResources());

        /* Verify that the event is only sent once */
        Utils.fastForward(200, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessageSentWhenCoalmineRunsOutOfCoalBeforeMonitoringStarts() throws Exception {

        /* Create game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMinableMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, SMALL, map);

        /* Remove all gold but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(COAL, point0) > 1) {
                map.mineMineralAtPoint(COAL, point0);
            }
        }

        /* Place a headquarters */
        Point point1 = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point1);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Place a road from headquarters to mine */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine);

        /* Deliver food to the miner */
        Utils.deliverCargo(mine, BREAD);
        Utils.deliverCargo(mine, FISH);
        Utils.deliverCargo(mine, MEAT);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to mine gold */
        Utils.fastForward(50, map);

        assertFalse(mine.isOutOfNaturalResources());

        /* Wait for the miner to leave the gold at the flag */
        assertEquals(miner.getTarget(), mine.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getFlag().getPosition());

        assertNull(miner.getCargo());

        Utils.fastForwardUntilWorkerReachesPoint(map, miner, mine.getPosition());

        assertTrue(miner.isInsideBuilding());
        assertEquals(player0.getMessages().size(), 0);

        /* Verify that the gold is gone and that the miner gets no gold */
        assertEquals(map.getAmountOfMineralAtPoint(COAL, point0), 0);

        /* Give the miner time to find out that there are no more resources */
        Utils.waitForNewMessage(player0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenStorageIsReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Deliver four planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(storage0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 1000; i++) {

            if (storage0.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(storage0.isReady());

        /* Verify that a message was sent */
        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        StoreHouseIsReadyMessage message = (StoreHouseIsReadyMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), STORE_HOUSE_IS_READY);
        assertEquals(message.building(), storage0);
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenStorageIsReadyIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Deliver four planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(storage0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 1000; i++) {

            if (storage0.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(storage0.isReady());

        /* Verify that a message was sent */
        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        StoreHouseIsReadyMessage message = (StoreHouseIsReadyMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), STORE_HOUSE_IS_READY);
        assertEquals(message.building(), storage0);

        /* Verify that the event is only sent once */
        Utils.fastForward(200, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessageSentWhenStorageIsReadyBeforeMonitoringStarts() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Deliver four planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo = new Cargo(STONE, map);

        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);

        /* Assign builder */
        Utils.assignBuilder(storage0);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 1000; i++) {

            if (storage0.isReady()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(storage0.isReady());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenTreeConservationProgramIsActivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Connect the armory to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that a message is sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        TreeConservationProgramActivatedMessage message = (TreeConservationProgramActivatedMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenTreeConservationProgramIsActivatedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Connect the armory to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that a message is sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        TreeConservationProgramActivatedMessage message = (TreeConservationProgramActivatedMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        /* Verify that the event is only sent once */
        Utils.fastForward(200, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessageSentWhenTreeConservationProgramIsActivatedBeforeMonitoringStarts() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that a message is sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenTreeConservationProgramIsDeactivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Wait for the tree conservation program message to be sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Put in more planks to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);

        /* Wait for the tree conservation program deactivation message to be sent */
        Utils.waitForNewMessage(player0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertNotNull(gameChangesList);
        assertNotNull(gameChangesList.newMessages());
        assertEquals(gameChangesList.newMessages().size(), 1);

        TreeConservationProgramDeactivatedMessage message = (TreeConservationProgramDeactivatedMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), TREE_CONSERVATION_PROGRAM_DEACTIVATED);
    }

    @Test
    public void testMonitoringEventWhenMessageSentWhenTreeConservationProgramIsDeactivatedIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Wait for the tree conservation program message to be sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Put in more planks to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);

        /* Wait for the tree conservation program deactivation message to be sent */
        Utils.waitForNewMessage(player0);

        GameChangesList gameChangesList = monitor.getLastEvent();

        assertEquals(gameChangesList.newMessages().size(), 1);

        TreeConservationProgramDeactivatedMessage message = (TreeConservationProgramDeactivatedMessage) gameChangesList.newMessages().getFirst();

        assertEquals(message.getMessageType(), TREE_CONSERVATION_PROGRAM_DEACTIVATED);

        /* Verify that the event is only sent once */
        Utils.fastForward(200, map);

        for (GameChangesList newChanges : monitor.getEventsAfterEvent(gameChangesList)) {
            assertEquals(newChanges.newMessages().size(), 0);
        }
    }

    @Test
    public void testNoMonitoringEventWhenMessageSentWhenTreeConservationProgramIsDeactivatedBeforeMonitoringStarts() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Wait for the tree conservation program message to be sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        /* Put in more planks to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);

        /* Wait for the tree conservation program deactivation message to be sent */
        Utils.waitForNewMessage(player0);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        map.stepTime();

        for (GameChangesList gameChangesList : monitor.getEvents()) {
            assertEquals(gameChangesList.newMessages().size(), 0);
        }
    }
}
