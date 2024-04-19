package org.appland.settlers.test;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Projectile;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.actors.CatapultWorker;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Miner;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Armory;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Brewery;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.Catapult;
import org.appland.settlers.model.buildings.CoalMine;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.messages.BombardedByCatapultMessage;
import org.appland.settlers.model.messages.BuildingCapturedMessage;
import org.appland.settlers.model.messages.BuildingLostMessage;
import org.appland.settlers.model.messages.GameEndedMessage;
import org.appland.settlers.model.messages.GeologistFindMessage;
import org.appland.settlers.model.messages.Message;
import org.appland.settlers.model.messages.MilitaryBuildingCausedLostLandMessage;
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

import static org.appland.settlers.model.DetailedVegetation.WATER;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.messages.Message.MessageType.*;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

public class TestMessages {

    /**
     * TODO:
     *  - We are being bombarded by a catapult
     *  - Game ended with draw
     *
     */

    @Test
    public void testNoMessagesOnStart() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Verify that there are no messages on start */
        assertTrue(player0.getMessages().isEmpty());
    }

    @Test
    public void testAMessageIsReceivedWhenBarracksIsReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        /* Verify that a message is sent when the barracks is finished */
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForwardUntilBuildingIsConstructed(barracks0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), Message.MessageType.MILITARY_BUILDING_READY);
        assertTrue(player0.getMessages().getFirst() instanceof MilitaryBuildingReadyMessage);

        MilitaryBuildingReadyMessage message = (MilitaryBuildingReadyMessage) player0.getMessages().getFirst();

        assertEquals(message.getBuilding(), barracks0);
    }

    @Test
    public void testNoMessageWhenNonMilitaryBuildingIsReady() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Connect the bakery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter.getFlag());

        /* Finish construction of the bakery */
        constructHouse(bakery);

        /* Verify that no message was sent */
        assertTrue(player0.getMessages().isEmpty());
    }

    @Test
    public void testBorderIsExtendedWhenBarracksIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), Message.MessageType.MILITARY_BUILDING_READY);

        Utils.fastForwardUntilWorkerReachesPoint(map, military, barracks0.getPosition());

        assertEquals(player0.getMessages().size(), 2);
        assertEquals(player0.getMessages().get(1).getMessageType(), Message.MessageType.MILITARY_BUILDING_OCCUPIED);
        assertTrue(player0.getMessages().get(1) instanceof MilitaryBuildingOccupiedMessage);

        MilitaryBuildingOccupiedMessage message = (MilitaryBuildingOccupiedMessage) player0.getMessages().get(1);

        assertEquals(message.getBuilding(), barracks0);
    }

    @Test
    public void testMessageSentWhenQuarryRunsOutOfResources() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), NO_MORE_RESOURCES);
        assertTrue(player0.getMessages().getFirst() instanceof NoMoreResourcesMessage);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) player0.getMessages().getFirst();

        assertEquals(message.getBuilding(), quarry0);
    }

    @Test
    public void testMessageSentWhenFishermanCanRunOutOfFish() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), NO_MORE_RESOURCES);
        assertTrue(player0.getMessages().getFirst() instanceof NoMoreResourcesMessage);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) player0.getMessages().getFirst();

        assertEquals(message.getBuilding(), fishery);
    }

    @Test
    public void testNoMessageWhenOtherBuildingRunsOutOfResources() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Unfinished sawmill doesn't need worker */
        assertFalse(sawmill.needsWorker());

        /* Finish construction of the sawmill */
        constructHouse(sawmill);

        assertTrue(sawmill.needsWorker());

        /* Verify that no message is sent when the sawmill runs out of resources */
        assertTrue(player0.getMessages().isEmpty());
    }

    @Test
    public void testMessageSentWhenGeologistFindsGoldOnMountain() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), GEOLOGIST_FIND);
        assertTrue(player0.getMessages().getFirst() instanceof GeologistFindMessage);

        GeologistFindMessage message = (GeologistFindMessage) player0.getMessages().getFirst();

        assertEquals(message.getPoint(), geologist.getPosition());
        assertEquals(message.getMaterial(), GOLD);
    }

    @Test
    public void testNoMessageSentWhenGeologistFindsNothingOnMountain() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        assertNull(sign.getType());
        assertEquals(player0.getMessages().size(), 0);
    }

    @Test
    public void testMessageSentWhenBarracksIsUnderAttack() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(41, 13);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(19, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(25, 13);
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
        int amountMessagesBefore = player1.getMessages().size();

        assertNotEquals(player0.getAvailableAttackersForBuilding(barracks1), 0);

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        assertEquals(player1.getMessages().size(), amountMessagesBefore + 1);
        assertEquals(player1.getMessages().getLast().getMessageType(), UNDER_ATTACK);
        assertTrue(player1.getMessages().getLast() instanceof UnderAttackMessage);

        UnderAttackMessage message = (UnderAttackMessage) player1.getMessages().getLast();

        assertEquals(message.getBuilding(), barracks1);
    }

    @Test
    public void testMessagesAreSentWhenAnAttackerTakesOverBuildingAfterWinningFight() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);

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

        /* Clear all soldiers from the inventories */
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

        /* Wait for the attacker to reach the barracks */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to come out from the barracks */
        Soldier defender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

        /* Wait for the general to beat the private */
        Utils.waitForFightToStart(map, attacker, defender);

        Utils.waitForSoldierToWinFight(attacker, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the attacker takes over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertTrue(player0.getMessages().size() >=  2);
        assertTrue(player1.getMessages().size() >= 3);

        int amountMessagesForPlayer0Before = player0.getMessages().size();
        int amountMessagesForPlayer1Before = player1.getMessages().size();

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(player0.getMessages().size(), amountMessagesForPlayer0Before + 1);
        assertEquals(player0.getMessages().getLast().getMessageType(), BUILDING_CAPTURED);
        assertTrue(player0.getMessages().getLast() instanceof BuildingCapturedMessage);

        BuildingCapturedMessage buildingCapturedMessage = (BuildingCapturedMessage) player0.getMessages().getLast();

        assertEquals(buildingCapturedMessage.getBuilding(), barracks1);

        assertTrue(player1.getMessages().size() >= amountMessagesForPlayer1Before);
        assertEquals(player1.getMessages().getLast().getMessageType(), BUILDING_LOST);
        assertTrue(player1.getMessages().getLast() instanceof BuildingLostMessage);

        BuildingLostMessage buildingLostMessage = (BuildingLostMessage) player1.getMessages().getLast();

        assertEquals(buildingLostMessage.getBuilding(), barracks1);
    }

    @Test
    public void testMessageSentWhenCoalmineRunsOutOfCoal() throws Exception {

        /* Create game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        /* Verify that a message is sent when the mine has run out of resources */
        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), NO_MORE_RESOURCES);
        assertTrue(player0.getMessages().getFirst() instanceof NoMoreResourcesMessage);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) player0.getMessages().getFirst();

        assertEquals(message.getBuilding(), mine);

        /* Verify that the mine is out of resources */
        assertTrue(mine.isOutOfNaturalResources());
    }

    @Test
    public void testMessageIsOnlySentOnceWhenCoalmineRunsOutOfCoal() throws Exception {

        /* Create game map with one player */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        /* Verify that a message is sent when the mine has run out of resources */
        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), NO_MORE_RESOURCES);
        assertTrue(player0.getMessages().getFirst() instanceof NoMoreResourcesMessage);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) player0.getMessages().getFirst();

        assertEquals(message.getBuilding(), mine);

        /* Verify that no more messages are sent */
        for (int i = 0; i < 500; i++){
            map.stepTime();

            assertEquals(player0.getMessages().size(), 1);
        }
    }

    @Test
    public void testMessageSentWhenStorageIsReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        /* Verify that a message was sent */
        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), STORE_HOUSE_IS_READY);
        assertTrue(player0.getMessages().getFirst() instanceof StoreHouseIsReadyMessage);

        StoreHouseIsReadyMessage message = (StoreHouseIsReadyMessage) player0.getMessages().getFirst();

        assertEquals(message.getBuilding(), storage0);
    }

    @Test
    public void testMessageSentWhenTreeConservationProgramIsActivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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
        assertEquals(player0.getMessages().getFirst().getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);
        assertTrue(player0.getMessages().getFirst() instanceof TreeConservationProgramActivatedMessage);

        TreeConservationProgramActivatedMessage message = (TreeConservationProgramActivatedMessage) player0.getMessages().getFirst();
    }

    @Test
    public void testOnlyOneMessageSentWhenTreeConservationProgramIsActivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 12);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Build two buildings that don't get resources */
        Point point2 = new Point(10, 10);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        Point point3 = new Point(10, 16);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point3);

        /* Connect the buildings to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());
        Road road1 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Verify that only one tree conservation program message is sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        for (int i = 0; i < 200; i++) {
            assertEquals(player0.getMessages().size(), 1);

            map.stepTime();
        }
    }

    @Test
    public void testMessageSentWhenTreeConservationProgramIsDeactivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
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

        assertEquals(player0.getMessages().size(), 2);
        assertEquals(player0.getMessages().get(1).getMessageType(), TREE_CONSERVATION_PROGRAM_DEACTIVATED);
        assertTrue(player0.getMessages().get(1) instanceof TreeConservationProgramDeactivatedMessage);

        TreeConservationProgramDeactivatedMessage message = (TreeConservationProgramDeactivatedMessage) player0.getMessages().get(1);
    }

    @Test
    public void testOnlyOneMessageSentWhenTreeConservationProgramIsDeactivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point21 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Place storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storehouse(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10);

        /* Build two buildings that doesn't get resources */
        Point point2 = new Point(10, 10);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        Point point3 = new Point(10, 18);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point3);

        /* Connect the buildings to the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());
        Road road1 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Wait for the tree conservation program message to be sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().getFirst().getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        /* Put in more planks to the headquarters */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50);

        /* Wait for the tree conservation program deactivation message to be sent */
        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 2);
        assertEquals(player0.getMessages().get(1).getMessageType(), TREE_CONSERVATION_PROGRAM_DEACTIVATED);
        assertTrue(player0.getMessages().get(1) instanceof TreeConservationProgramDeactivatedMessage);

        TreeConservationProgramDeactivatedMessage message = (TreeConservationProgramDeactivatedMessage) player0.getMessages().get(1);

        /* Verify that only one message is sent */
        for (int i = 0; i < 200; i++) {
            map.stepTime();

            assertEquals(player0.getMessages().size(), 2);
        }
    }

    @Test
    public void testThisBuildingHasCausedYouToLoseLandWhenBarracksIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarters */
        Point point1 = new Point(21, 23);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(fortress0);

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

        /* Verify a message is sent when the barracks is populated so player 1 loses land */
        Point point3 = new Point(12, 18);
        assertTrue(player1.getBorderPoints().contains(point3));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, fortress0.getPosition());

        assertFalse(player1.getBorderPoints().contains(point3));
        assertEquals(player1.getMessages().size(), 1);
        assertEquals(player1.getMessages().getFirst().getMessageType(), MILITARY_BUILDING_CAUSED_LOST_LAND);

        MilitaryBuildingCausedLostLandMessage message = (MilitaryBuildingCausedLostLandMessage) player1.getMessages().getFirst();

        assertEquals(message.getBuilding(), fortress0);
    }

    @Test
    public void testOnlyOneMessageSentWhenThisBuildingHasCausedYouToLoseLandWhenBarracksIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarters */
        Point point1 = new Point(25, 25);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point22 = new Point(5, 23);
        Fortress fortress0 = map.placeBuilding(new Fortress(player0), point22);

        /* Place road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), fortress0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(fortress0);

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

        /* Verify a message is sent when the barracks is populated so player 1 loses land */
        Point point3 = new Point(14, 20);

        assertTrue(player1.getBorderPoints().contains(point3));

        Utils.fastForwardUntilWorkerReachesPoint(map, military, fortress0.getPosition());

        assertFalse(player1.getBorderPoints().contains(point3));
        assertEquals(player1.getMessages().size(), 1);
        assertEquals(player1.getMessages().getFirst().getMessageType(), MILITARY_BUILDING_CAUSED_LOST_LAND);

        MilitaryBuildingCausedLostLandMessage message = (MilitaryBuildingCausedLostLandMessage) player1.getMessages().getFirst();

        assertEquals(message.getBuilding(), fortress0);

        /* Verify that only one message is sent */
        Utils.fastForward(10, map);

        if (player1.getMessages().size() > 1) {
            for (Message newMessage : player1.getMessages()) {
                assertFalse(newMessage instanceof MilitaryBuildingCausedLostLandMessage);
                assertNotEquals(newMessage.getMessageType(), MILITARY_BUILDING_CAUSED_LOST_LAND);
            }
        }
    }

    @Test
    public void testMessageSentWhenPlayerWins() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(39, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(19, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction */
        constructHouse(fortress0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, fortress0);

        /* Empty all soldiers from the second player's headquarters */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.getAvailableAttackersForBuilding(headquarter1) > 0);

        player0.attack(headquarter1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), headquarter1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getFlag().getPosition());

        map.stepTime();

        assertEquals(attacker.getPosition(), headquarter1.getFlag().getPosition());

        /* Verify that the headquarters is destroyed and the first player won the game */
        assertEquals(headquarter1.getNumberOfHostedSoldiers(), 0);
        assertEquals(headquarter1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), headquarter1.getPosition());
        assertNull(map.getWinner());

        for (Message message : player0.getMessages()) {
            assertNotEquals(message.getMessageType(), GAME_ENDED);
        }

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getPosition());

        map.stepTime();

        assertEquals(map.getWinner(), player0);
        assertEquals(player1.getBuildings().size(), 1);
        assertTrue(player1.getBuildings().getFirst().isBurningDown());
        assertEquals(player0.getMessages().getLast().getMessageType(), GAME_ENDED);

        GameEndedMessage message = (GameEndedMessage) player0.getMessages().getLast();

        assertEquals(message.getWinner(), player0);
    }

    @Test
    public void testOnlyOneMessageSentWhenPlayerWins() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(39, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(19, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction */
        constructHouse(fortress0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, fortress0);

        /* Empty all soldiers from the second player's headquarters */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.getAvailableAttackersForBuilding(headquarter1) > 0);

        player0.attack(headquarter1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), headquarter1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getFlag().getPosition());

        map.stepTime();

        assertEquals(attacker.getPosition(), headquarter1.getFlag().getPosition());

        /* Verify that the headquarters is destroyed and the first player won the game */
        assertEquals(headquarter1.getNumberOfHostedSoldiers(), 0);
        assertEquals(headquarter1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), headquarter1.getPosition());
        assertNull(map.getWinner());

        for (Message message : player0.getMessages()) {
            assertNotEquals(message.getMessageType(), GAME_ENDED);
        }

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getPosition());

        map.stepTime();

        assertEquals(map.getWinner(), player0);
        assertEquals(player1.getBuildings().size(), 1);
        assertTrue(player1.getBuildings().getFirst().isBurningDown());
        assertEquals(player0.getMessages().getLast().getMessageType(), GAME_ENDED);

        GameEndedMessage message = (GameEndedMessage) player0.getMessages().getLast();

        assertEquals(message.getWinner(), player0);

        /* Verify that only one message is sent for the event */
        Utils.fastForward(20, map);

        int gameEndedMessages = 0;
        for (Message newMessage : player0.getMessages()) {
            if (newMessage.getMessageType() == GAME_ENDED) {
                gameEndedMessages = gameEndedMessages + 1;
            }
        }

        assertEquals(gameEndedMessages, 1);
    }

    @Test
    public void testMessageSentToEachPlayerWhenPlayerWins() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.GREEN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);

        /* Create game map choosing two players */
        GameMap map = new GameMap(players, 100, 100);

        /* Place player 0's headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place player 1's headquarters */
        Point point1 = new Point(39, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks for player 0 */
        Point point2 = new Point(19, 5);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        /* Finish construction */
        constructHouse(fortress0);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, fortress0);

        /* Empty all soldiers from the second player's headquarters */
        Utils.adjustInventoryTo(headquarter1, PRIVATE, 0);
        Utils.adjustInventoryTo(headquarter1, SERGEANT, 0);
        Utils.adjustInventoryTo(headquarter1, GENERAL, 0);

        /* Order an attack */
        assertTrue(player0.getAvailableAttackersForBuilding(headquarter1) > 0);

        player0.attack(headquarter1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);

        /* Wait for the attacker to get to the attacked buildings flag */
        assertEquals(attacker.getTarget(), headquarter1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getFlag().getPosition());

        map.stepTime();

        assertEquals(attacker.getPosition(), headquarter1.getFlag().getPosition());

        /* Verify that the headquarters is destroyed and the first player won the game */
        assertEquals(headquarter1.getNumberOfHostedSoldiers(), 0);
        assertEquals(headquarter1.getPlayer(), player1);
        assertEquals(attacker.getTarget(), headquarter1.getPosition());
        assertNull(map.getWinner());

        for (Message message : player0.getMessages()) {
            assertNotEquals(message.getMessageType(), GAME_ENDED);
        }

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, headquarter1.getPosition());

        map.stepTime();

        assertEquals(map.getWinner(), player0);
        assertEquals(player1.getBuildings().size(), 1);
        assertTrue(player1.getBuildings().getFirst().isBurningDown());
        assertEquals(player0.getMessages().getLast().getMessageType(), GAME_ENDED);

        GameEndedMessage messageForPlayer0 = null;
        GameEndedMessage messageForPlayer1 = null;

        for (Message message : player0.getMessages()) {
            if (message.getMessageType() == GAME_ENDED) {
                messageForPlayer0 = (GameEndedMessage) message;
            }
        }

        for (Message message : player1.getMessages()) {
            if (message.getMessageType() == GAME_ENDED) {
                messageForPlayer1 = (GameEndedMessage) message;
            }
        }

        assertNotNull(messageForPlayer0);
        assertNotNull(messageForPlayer1);
        assertEquals(messageForPlayer0.getWinner(), player0);
        assertEquals(messageForPlayer1.getWinner(), player0);
    }

    @Test
    public void testMessageSentWhenCatapultHitsBarracks() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarters */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(33, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the woodcutter */
        constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Place catapult */
        Point point3 = new Point(19, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarters */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Verify that a message is sent when the catapult destroys the barracks */
        for (int i = 0; i < 1000; i++) {

            /* Deliver stone to the catapult */
            catapult.putCargo(new Cargo(STONE, map));

            /* Wait for the catapult to throw a projectile */
            Projectile projectile = Utils.waitForCatapultToThrowProjectile(catapult);

            /* Wait for the projectile to reach its target */
            Utils.waitForProjectileToReachTarget(projectile, map);

            /* Check if the projectile hit the barracks */
            if (barracks0.getNumberOfHostedSoldiers() == 0) {
                break;
            }
        }

        assertEquals(barracks0.getNumberOfHostedSoldiers(), 0);

        int numberOfBombardedByCatapultMessages = 0;
        BombardedByCatapultMessage bombardedByCatapultMessage = null;

        for (Message message : player1.getMessages()) {
            if (message.getMessageType() == BOMBARDED_BY_CATAPULT) {
                numberOfBombardedByCatapultMessages = numberOfBombardedByCatapultMessages + 1;
                bombardedByCatapultMessage = (BombardedByCatapultMessage) message;
            }
        }

        assertEquals(numberOfBombardedByCatapultMessages, 1);
        assertNotNull(bombardedByCatapultMessage);
        assertEquals(bombardedByCatapultMessage.getCatapult(), catapult);
        assertEquals(bombardedByCatapultMessage.getHitBuilding(), barracks0);
    }

    @Test
    public void testMessageSentWhenCatapultHitsBarracksIsOnlySentOnce() throws Exception {

        /* Create new game map */
        Player player0 = new Player("Player 0", PlayerColor.BLUE);
        Player player1 = new Player("Player 1", PlayerColor.RED);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        players.add(player1);
        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(9, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place headquarters */
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        /* Place barracks */
        Point point2 = new Point(33, 5);
        Barracks barracks0 = map.placeBuilding(new Barracks(player1), point2);

        /* Finish construction of the woodcutter */
        constructHouse(barracks0);

        /* Occupy the barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Place catapult */
        Point point3 = new Point(19, 5);
        Catapult catapult = map.placeBuilding(new Catapult(player0), point3);

        /* Finish construction of the catapult */
        constructHouse(catapult);

        /* Occupy the catapult */
        Worker catapultWorker0 = Utils.occupyBuilding(new CatapultWorker(player0, map), catapult);

        assertTrue(catapultWorker0.isInsideBuilding());
        assertEquals(catapultWorker0.getHome(), catapult);
        assertEquals(catapult.getWorker(), catapultWorker0);

        /* Remove all the stones in the headquarters */
        Utils.adjustInventoryTo(headquarter0, STONE, 0);

        /* Verify that a message is sent when the catapult destroys the barracks */
        for (int i = 0; i < 1000; i++) {

            /* Deliver stone to the catapult */
            catapult.putCargo(new Cargo(STONE, map));

            /* Wait for the catapult to throw a projectile */
            Projectile projectile = Utils.waitForCatapultToThrowProjectile(catapult);

            /* Wait for the projectile to reach its target */
            Utils.waitForProjectileToReachTarget(projectile, map);

            /* Check if the projectile hit the barracks */
            if (barracks0.getNumberOfHostedSoldiers() == 0) {
                break;
            }
        }

        assertEquals(barracks0.getNumberOfHostedSoldiers(), 0);

        int numberOfBombardedByCatapultMessages = 0;
        BombardedByCatapultMessage bombardedByCatapultMessage = null;

        for (Message message : player1.getMessages()) {
            if (message.getMessageType() == BOMBARDED_BY_CATAPULT) {
                numberOfBombardedByCatapultMessages = numberOfBombardedByCatapultMessages + 1;
                bombardedByCatapultMessage = (BombardedByCatapultMessage) message;
            }
        }

        assertEquals(numberOfBombardedByCatapultMessages, 1);
        assertNotNull(bombardedByCatapultMessage);
        assertEquals(bombardedByCatapultMessage.getCatapult(), catapult);
        assertEquals(bombardedByCatapultMessage.getHitBuilding(), barracks0);

        /* Verify that the message is only sent once */
        Utils.fastForward(30, map);

        numberOfBombardedByCatapultMessages = 0;
        for (Message message : player1.getMessages()) {
            if (message.getMessageType() == BOMBARDED_BY_CATAPULT) {
                numberOfBombardedByCatapultMessages = numberOfBombardedByCatapultMessages + 1;
            }
        }

        assertEquals(numberOfBombardedByCatapultMessages, 1);
    }
}
