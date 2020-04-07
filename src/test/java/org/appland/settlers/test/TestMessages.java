package org.appland.settlers.test;

import org.appland.settlers.model.Armory;
import org.appland.settlers.model.Bakery;
import org.appland.settlers.model.Barracks;
import org.appland.settlers.model.Brewery;
import org.appland.settlers.model.Building;
import org.appland.settlers.model.BuildingCapturedMessage;
import org.appland.settlers.model.BuildingLostMessage;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.CoalMine;
import org.appland.settlers.model.Fisherman;
import org.appland.settlers.model.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Geologist;
import org.appland.settlers.model.GeologistFindMessage;
import org.appland.settlers.model.Headquarter;
import org.appland.settlers.model.Message;
import org.appland.settlers.model.Military;
import org.appland.settlers.model.MilitaryBuildingOccupiedMessage;
import org.appland.settlers.model.MilitaryBuildingReadyMessage;
import org.appland.settlers.model.Miner;
import org.appland.settlers.model.NoMoreResourcesMessage;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Sawmill;
import org.appland.settlers.model.Sign;
import org.appland.settlers.model.Stonemason;
import org.appland.settlers.model.Storage;
import org.appland.settlers.model.StoreHouseIsReadyMessage;
import org.appland.settlers.model.TreeConservationProgramActivatedMessage;
import org.appland.settlers.model.TreeConservationProgramDeactivatedMessage;
import org.appland.settlers.model.UnderAttackMessage;
import org.appland.settlers.model.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.Material.BREAD;
import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.FISH;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.MEAT;
import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.PRIVATE;
import static org.appland.settlers.model.Material.STONE;
import static org.appland.settlers.model.Message.MessageType.BUILDING_CAPTURED;
import static org.appland.settlers.model.Message.MessageType.BUILDING_LOST;
import static org.appland.settlers.model.Message.MessageType.GEOLOGIST_FIND;
import static org.appland.settlers.model.Message.MessageType.NO_MORE_RESOURCES;
import static org.appland.settlers.model.Message.MessageType.STORE_HOUSE_IS_READY;
import static org.appland.settlers.model.Message.MessageType.TREE_CONSERVATION_PROGRAM_ACTIVATED;
import static org.appland.settlers.model.Message.MessageType.TREE_CONSERVATION_PROGRAM_DEACTIVATED;
import static org.appland.settlers.model.Message.MessageType.UNDER_ATTACK;
import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.PRIVATE_RANK;
import static org.appland.settlers.model.Size.LARGE;
import static org.appland.settlers.model.Size.SMALL;
import static org.appland.settlers.model.Tile.Vegetation.WATER;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestMessages {

    @Test
    public void testNoMessagesOnStart() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Verify that there are no messages on start */
        assertTrue(player0.getMessages().isEmpty());
    }

    @Test
    public void testAMessageIsReceivedWhenBarracksIsReady() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Verify that a message is sent when the barracks is finished */
        assertTrue(player0.getMessages().isEmpty());

        Utils.fastForwardUntilBuildingIsConstructed(barracks0, map);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().get(0).getMessageType(), Message.MessageType.MILITARY_BUILDING_READY);
        assertTrue(player0.getMessages().get(0) instanceof MilitaryBuildingReadyMessage);

        MilitaryBuildingReadyMessage message = (MilitaryBuildingReadyMessage) player0.getMessages().get(0);

        assertEquals(message.getBuilding(), barracks0);
    }

    @Test
    public void testNoMessageWhenNonMilitaryBuildingIsReady() throws Exception {

        /* Create new single player game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place bakery */
        Point point3 = new Point(7, 9);
        Building bakery = map.placeBuilding(new Bakery(player0), point3);

        /* Connect the bakery with the headquarter */
        Point point4 = new Point(8, 8);
        Point point5 = new Point(7, 7);
        Point point6 = new Point(8, 6);
        Point point7 = new Point(7, 5);
        Point point8 = new Point(6, 4);
        Road road0 = map.placeRoad(player0, point4, point5, point6, point7, point8);

        /* Finish construction of the bakery */
        Utils.constructHouse(bakery, map);

        /* Verify that no message was sent */
        assertTrue(player0.getMessages().isEmpty());
    }

    @Test
    public void testBorderIsExtendedWhenBarracksIsPopulated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        /* Create game map */
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing barracks */
        Point point22 = new Point(5, 23);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point22);

        /* Placing road */
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), barracks0.getFlag());

        /* Wait for the barracks to finish construction */
        Utils.fastForwardUntilBuildingIsConstructed(barracks0, map);

        /* Wait for a soldier to walk to the barracks */
        assertTrue(headquarter0.getAmount(PRIVATE) > 0);

        map.stepTime();

        Utils.verifyListContainsWorkerOfType(map.getWorkers(), Military.class);

        Military military = null;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof Military) {
                military = (Military)worker;
            }
        }

        assertNotNull(military);

        /* Verify a message is sent when the barracks is populated */
        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().get(0).getMessageType(), Message.MessageType.MILITARY_BUILDING_READY);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(7, 9);
        Building quarry0 = map.placeBuilding(new Quarry(player0), point1);

        /* Finish construction of the quarry */
        Utils.constructHouse(quarry0, map);

        /* Populate the quarry */
        Worker stonemason = Utils.occupyBuilding(new Stonemason(player0, map), quarry0, map);

        assertTrue(stonemason.isInsideBuilding());
        assertEquals(stonemason.getHome(), quarry0);
        assertEquals(quarry0.getWorker(), stonemason);

        /* Connect the quarry with the headquarter */
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
        assertEquals(player0.getMessages().get(0).getMessageType(), Message.MessageType.NO_MORE_RESOURCES);
        assertTrue(player0.getMessages().get(0) instanceof NoMoreResourcesMessage);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) player0.getMessages().get(0);

        assertEquals(message.getBuilding(), quarry0);
    }

    @Test
    public void testMessageSentWhenFishermanCanRunOutOfFish() throws Exception {

        /* Create new game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point0 = new Point(4, 4);
        Point point1 = new Point(6, 4);
        Point point2 = new Point(5, 5);

        map.getTerrain().getTileBelow(point2).setVegetationType(WATER);

        /* Remove fishes until there is only one left */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountFishAtPoint(point0) == 1) {
                break;
            }

            map.catchFishAtPoint(point0);
        }

        /* Place headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place fishery */
        Point point3 = new Point(7, 5);
        Building fishery = map.placeBuilding(new Fishery(player0), point3);

        /* Place a road from the headquarter to the fishery */
        map.placeAutoSelectedRoad(player0, headquarter.getFlag(), fishery.getFlag());

        /* Construct the fisherman hut */
        constructHouse(fishery, map);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishery, map);

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
        assertFalse(fishery.outOfNaturalResources());

        Utils.fastForward(20, map);

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
        assertEquals(player0.getMessages().get(0).getMessageType(), Message.MessageType.NO_MORE_RESOURCES);
        assertTrue(player0.getMessages().get(0) instanceof NoMoreResourcesMessage);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) player0.getMessages().get(0);

        assertEquals(message.getBuilding(), fishery);
    }

    @Test
    public void testNoMessageWhenOtherBuildingRunsOutOfResources() throws Exception {
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarter */
        Point point0 = new Point(5, 5);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place sawmill */
        Point point3 = new Point(7, 9);
        Building sawmill = map.placeBuilding(new Sawmill(player0), point3);

        /* Unfinished sawmill doesn't need worker */
        assertFalse(sawmill.needsWorker());

        /* Finish construction of the sawmill */
        Utils.constructHouse(sawmill, map);

        assertTrue(sawmill.needsWorker());

        /* Verify that no message is sent when the sawmill runs out of resources */
        assertTrue(player0.getMessages().isEmpty());
    }

    @Test
    public void testMessageSentWhenGeologistFindsGoldOnMountain() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 9, map);
        Utils.putMineralWithinRadius(GOLD, point1, 9, map);

        /* Connect headquarter and flag */
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
        assertEquals(player0.getMessages().get(0).getMessageType(), GEOLOGIST_FIND);
        assertTrue(player0.getMessages().get(0) instanceof GeologistFindMessage);

        GeologistFindMessage message = (GeologistFindMessage) player0.getMessages().get(0);

        assertEquals(message.getPoint(), geologist.getPosition());
        assertEquals(message.getMaterial(), GOLD);
    }

    @Test
    public void testNoMessageSentWhenGeologistFindsNothingOnMountain() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Placing flag */
        Point point1 = new Point(15, 15);
        Flag flag = map.placeFlag(player0, point1);

        /* Create a mountain with gold */
        Utils.createMountainWithinRadius(point1, 9, map);

        /* Connect headquarter and flag */
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

        assertEquals(sign.getType(), null);
        assertEquals(player0.getMessages().size(), 0);
    }

    @Test
    public void testMessageSentWhenBarracksIsUnderAttack() throws Exception {

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
        Point point0 = new Point(5, 5);
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
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0, map);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Verify that no militaries leave the barracks before the attack is
         initiated */
        for (int i = 0; i < 100; i++) {
            for (Worker worker : map.getWorkers()) {
                if (worker instanceof Military) {
                    assertTrue(worker.isInsideBuilding());
                }
            }

            map.stepTime();
        }

        /* Verify that a message is sent to player 1 when it's attacked */
        assertEquals(player1.getMessages().size(), 2);

        player0.attack(barracks1, 1);

        assertEquals(player1.getMessages().size(), 3);
        assertEquals(player1.getMessages().get(2).getMessageType(), UNDER_ATTACK);
        assertTrue(player1.getMessages().get(2) instanceof UnderAttackMessage);

        UnderAttackMessage message = (UnderAttackMessage) player1.getMessages().get(2);

        assertEquals(message.getBuilding(), barracks1);

        /* Verify that a military leaves the barracks before the attack when
         initiated */
        map.stepTime();

        List<Military> militaryOutside = Utils.findWorkersOfTypeOutsideForPlayer(Military.class, player0, map);

        assertEquals(militaryOutside.size(), 1);
        assertEquals(barracks0.getNumberOfHostedMilitary(), 1);
    }

    @Test
    public void testMessagesAreSentWhenAnAttackerTakesOverBuildingAfterWinningFight() throws Exception {

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
        Point point0 = new Point(5, 5);
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
        Utils.constructHouse(barracks0, map);
        Utils.constructHouse(barracks1, map);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0, map);

        /* Populate player 1's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1, map);

        /* Order an attack */
        player0.attack(barracks1, 1);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Military attacker = Utils.findMilitaryOutsideBuilding(player0, map);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);

        /* Verify that a military leaves the attacked building to defend when
         the attacker reaches the flag */
        assertEquals(barracks1.getNumberOfHostedMilitary(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedMilitary(), 0);

        /* Wait for the defender to go to the attacker */
        Military defender = Utils.findMilitaryOutsideBuilding(player1, map);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Wait for the general to beat the private */
        Utils.waitForWorkerToDisappear(defender, map);

        assertFalse(map.getWorkers().contains(defender));

        /* Wait for the attacker to go back to the fixed point */
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, attacker.getTarget());

        /* Verify that the attacker takes over the building */
        assertEquals(attacker.getTarget(), barracks1.getPosition());
        assertEquals(player0.getMessages().size(), 2);
        assertEquals(player1.getMessages().size(), 3);

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getPosition());

        assertEquals(barracks1.getPlayer(), player0);
        assertEquals(player0.getMessages().size(), 3);
        assertEquals(player0.getMessages().get(2).getMessageType(), BUILDING_CAPTURED);
        assertTrue(player0.getMessages().get(2) instanceof BuildingCapturedMessage);

        BuildingCapturedMessage buildingCapturedMessage = (BuildingCapturedMessage) player0.getMessages().get(2);

        assertEquals(buildingCapturedMessage.getBuilding(), barracks1);

        assertEquals(player1.getMessages().size(), 4);
        assertEquals(player1.getMessages().get(3).getMessageType(), BUILDING_LOST);
        assertTrue(player1.getMessages().get(3) instanceof BuildingLostMessage);

        BuildingLostMessage buildingLostMessage = (BuildingLostMessage) player1.getMessages().get(3);

        assertEquals(buildingLostMessage.getBuilding(), barracks1);

    }

    @Test
    public void testMessageSentWhenCoalmineRunsOutOfCoal() throws Exception {

        /* Create game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, SMALL, map);

        /* Remove all gold but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(COAL, point0) > 1) {
                map.mineMineralAtPoint(COAL, point0);
            }
        }

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food to the miner */
        Utils.deliverCargo(mine, BREAD);
        Utils.deliverCargo(mine, FISH);
        Utils.deliverCargo(mine, MEAT);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to mine gold */
        Utils.fastForward(50, map);

        assertFalse(mine.outOfNaturalResources());

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
        assertEquals(player0.getMessages().get(0).getMessageType(), NO_MORE_RESOURCES);
        assertTrue(player0.getMessages().get(0) instanceof NoMoreResourcesMessage);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) player0.getMessages().get(0);

        assertEquals(message.getBuilding(), mine);

        /* Verify that the mine is out of resources */
        assertTrue(mine.outOfNaturalResources());
    }

    @Test
    public void testMessageIsOnlySentOnceWhenCoalmineRunsOutOfCoal() throws Exception {

        /* Create game map with one player */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Put a small mountain on the map */
        Point point0 = new Point(10, 8);
        Utils.surroundPointWithMountain(point0, map);
        Utils.putCoalAtSurroundingTiles(point0, SMALL, map);

        /* Remove all gold but one */
        for (int i = 0; i < 1000; i++) {
            if (map.getAmountOfMineralAtPoint(COAL, point0) > 1) {
                map.mineMineralAtPoint(COAL, point0);
            }
        }

        /* Place a headquarter */
        Point hqPoint = new Point(15, 15);
        Headquarter building0 = map.placeBuilding(new Headquarter(player0), hqPoint);

        /* Place a gold mine */
        Building mine = map.placeBuilding(new CoalMine(player0), point0);

        /* Place a road from headquarter to mine */
        map.placeAutoSelectedRoad(player0, building0.getFlag(), mine.getFlag());

        /* Construct the gold mine */
        constructHouse(mine, map);

        /* Deliver food to the miner */
        Utils.deliverCargo(mine, BREAD);
        Utils.deliverCargo(mine, FISH);
        Utils.deliverCargo(mine, MEAT);

        /* Manually place miner */
        Miner miner = new Miner(player0, map);

        Utils.occupyBuilding(miner, mine, map);

        assertTrue(miner.isInsideBuilding());

        /* Wait for the miner to rest */
        Utils.fastForward(100, map);

        /* Wait for the miner to mine gold */
        Utils.fastForward(50, map);

        assertFalse(mine.outOfNaturalResources());

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
        assertEquals(player0.getMessages().get(0).getMessageType(), NO_MORE_RESOURCES);
        assertTrue(player0.getMessages().get(0) instanceof NoMoreResourcesMessage);

        NoMoreResourcesMessage message = (NoMoreResourcesMessage) player0.getMessages().get(0);

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
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storage(player0), point22);

        /* Deliver four planks and two stones */
        Cargo plankCargo = new Cargo(PLANK, map);
        Cargo stoneCargo  = new Cargo(STONE, map);

        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(plankCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);
        storage0.putCargo(stoneCargo);

        /* Verify that this is not enough to construct the storage */
        for (int i = 0; i < 1000; i++) {

            if (storage0.ready()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(storage0.ready());

        /* Verify that a message was sent */
        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().get(0).getMessageType(), STORE_HOUSE_IS_READY);
        assertTrue(player0.getMessages().get(0) instanceof StoreHouseIsReadyMessage);

        StoreHouseIsReadyMessage message = (StoreHouseIsReadyMessage) player0.getMessages().get(0);

        assertEquals(message.getBuilding(), storage0);
    }

    @Test
    public void testMessageSentWhenTreeConservationProgramIsActivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storage(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10, map);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Verify that a message is sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);
        System.out.println(player0.getMessages());
        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().get(0).getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);
        assertTrue(player0.getMessages().get(0) instanceof TreeConservationProgramActivatedMessage);

        TreeConservationProgramActivatedMessage message = (TreeConservationProgramActivatedMessage) player0.getMessages().get(0);

        assertEquals(message.getBuilding(), headquarter0);
    }

    @Test
    public void testOnlyOneMessageSentWhenTreeConservationProgramIsActivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storage(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10, map);

        /* Build two buildings that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        Point point3 = new Point(10, 16);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point3);

        /* Connect the buildings to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());
        Road road1 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Verify that only one tree conservation program message is sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().get(0).getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        for (int i = 0; i < 200; i++) {
            assertEquals(player0.getMessages().size(), 1);

            map.stepTime();
        }
    }

    @Test
    public void testMessageSentWhenTreeConservationProgramIsDeactivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storage(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10, map);

        /* Try to build a building that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        /* Connect the armory to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());

        /* Wait for the tree conservation program message to be sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().get(0).getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        /* Put in more planks to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50, map);

        /* Wait for the tree conservation program deactivation message to be sent */
        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 2);
        assertEquals(player0.getMessages().get(1).getMessageType(), TREE_CONSERVATION_PROGRAM_DEACTIVATED);
        assertTrue(player0.getMessages().get(1) instanceof TreeConservationProgramDeactivatedMessage);

        TreeConservationProgramDeactivatedMessage message = (TreeConservationProgramDeactivatedMessage) player0.getMessages().get(1);

        assertEquals(message.getBuilding(), headquarter0);

    }

    @Test
    public void testOnlyOneMessageSentWhenTreeConservationProgramIsDeactivated() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Placing headquarter */
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        /* Placing storage */
        Point point22 = new Point(6, 22);
        Building storage0 = map.placeBuilding(new Storage(player0), point22);

        /* Set the amount of planks to the limit for the tree conservation program */
        Utils.adjustInventoryTo(headquarter0, PLANK, 10, map);

        /* Build two buildings that doesn't get resources */
        Point point2 = new Point(10, 6);
        Armory armory0 = map.placeBuilding(new Armory(player0), point2);

        Point point3 = new Point(10, 16);
        Brewery brewery0 = map.placeBuilding(new Brewery(player0), point3);

        /* Connect the buildings to the headquarter */
        Road road0 = map.placeAutoSelectedRoad(player0, armory0.getFlag(), headquarter0.getFlag());
        Road road1 = map.placeAutoSelectedRoad(player0, brewery0.getFlag(), headquarter0.getFlag());

        /* Wait for the tree conservation program message to be sent */
        assertTrue(player0.getMessages().isEmpty());

        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 1);
        assertEquals(player0.getMessages().get(0).getMessageType(), TREE_CONSERVATION_PROGRAM_ACTIVATED);

        /* Put in more planks to the headquarter */
        Utils.adjustInventoryTo(headquarter0, PLANK, 50, map);

        /* Wait for the tree conservation program deactivation message to be sent */
        Utils.waitForNewMessage(player0);

        assertEquals(player0.getMessages().size(), 2);
        assertEquals(player0.getMessages().get(1).getMessageType(), TREE_CONSERVATION_PROGRAM_DEACTIVATED);
        assertTrue(player0.getMessages().get(1) instanceof TreeConservationProgramDeactivatedMessage);

        TreeConservationProgramDeactivatedMessage message = (TreeConservationProgramDeactivatedMessage) player0.getMessages().get(1);

        assertEquals(message.getBuilding(), headquarter0);

        /* Verify that only one message is sent */
        for (int i = 0; i < 200; i++) {
            map.stepTime();

            assertEquals(player0.getMessages().size(), 2);
        }
    }
}
