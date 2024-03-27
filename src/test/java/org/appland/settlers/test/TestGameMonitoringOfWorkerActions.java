package org.appland.settlers.test;

import org.appland.settlers.assets.CropType;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.GameChangesList;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.StoneType;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.TreeSize;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.actors.Worker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static org.appland.settlers.model.BodyType.FAT;
import static org.appland.settlers.model.BodyType.THIN;
import static org.appland.settlers.model.Crop.GrowthState.JUST_PLANTED;
import static org.appland.settlers.model.DetailedVegetation.WATER;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.WorkerAction.CHEW_GUM;
import static org.appland.settlers.model.WorkerAction.READ_NEWSPAPER;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

public class TestGameMonitoringOfWorkerActions {

    @Test
    public void testMonitoringEventWhenForesterPlantsTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the forester to get to the point where he will plant a tree */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        /* Wait for the forester to finish planting the tree */
        Utils.waitForForesterToStopPlantingTree(forester, map);

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());

        /* Verify that an event was sent when the forester started planting the tree */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(forester)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(forester);

                assertEquals(workerAction, WorkerAction.PLANTING_TREE);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenForesterPlantsTreeIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place forester hut */
        Point point1 = new Point(10, 4);
        Building foresterHut = map.placeBuilding(new ForesterHut(player0), point1);

        /* Construct the forester hut */
        constructHouse(foresterHut);

        /* Manually place forester */
        Forester forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        /* Let the forester rest */
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        Point point = forester.getTarget();

        assertTrue(forester.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the forester to get to the point where he will plant a tree */
        Utils.fastForwardUntilWorkersReachTarget(map, forester);

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());

        /* Wait for the forester to finish planting the tree */
        Utils.waitForForesterToStopPlantingTree(forester, map);

        assertFalse(forester.isPlanting());
        assertTrue(map.isTreeAtPoint(point));
        assertNull(forester.getCargo());

        /* Verify that an event was sent when the forester started planting the tree */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(forester)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(forester);

                assertEquals(workerAction, WorkerAction.PLANTING_TREE);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithStartedActions().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenWoodcutterCutsDownTree() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker woodcutterWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(woodcutterWorker, woodcutter);

        /* Wait for the woodcutter to rest */
        Utils.fastForward(99, map);

        assertTrue(woodcutterWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());

        Point point = woodcutterWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(woodcutterWorker.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, woodcutterWorker);

        assertTrue(woodcutterWorker.isArrived());
        assertTrue(woodcutterWorker.isAt(point));

        map.stepTime();

        assertTrue(woodcutterWorker.isCuttingTree());
        assertNull(woodcutterWorker.getCargo());

        /* Wait for the woodcutter to finish cutting the tree */
        for (int i = 0; i < 49; i++) {
            assertTrue(woodcutterWorker.isCuttingTree());
            assertTrue(map.isTreeAtPoint(point));
            map.stepTime();
        }

        /* Verify that the woodcutter stopped cutting */
        assertFalse(woodcutterWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(woodcutterWorker.getCargo());
        assertEquals(woodcutterWorker.getCargo().getMaterial(), WOOD);

        /* Verify that an event was sent when the forester started planting the tree */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(woodcutterWorker)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(woodcutterWorker);

                assertEquals(workerAction, WorkerAction.CUTTING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenWoodcutterCutsDownTreeIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place and grow the tree */
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        /* Place the woodcutter */
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        /* Construct the forester hut */
        constructHouse(woodcutter);

        /* Manually place forester */
        WoodcutterWorker woodcutterWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(woodcutterWorker, woodcutter);

        /* Wait for the woodcutter to rest */
        Utils.fastForward(99, map);

        assertTrue(woodcutterWorker.isInsideBuilding());

        /* Step once and make sure the forester goes out of the hut */
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());

        Point point = woodcutterWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(woodcutterWorker.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the woodcutter reach the tree and start cutting */
        Utils.fastForwardUntilWorkersReachTarget(map, woodcutterWorker);

        assertTrue(woodcutterWorker.isArrived());
        assertTrue(woodcutterWorker.isAt(point));

        map.stepTime();

        assertTrue(woodcutterWorker.isCuttingTree());
        assertNull(woodcutterWorker.getCargo());

        /* Wait for the woodcutter to finish cutting the tree */
        for (int i = 0; i < 49; i++) {
            assertTrue(woodcutterWorker.isCuttingTree());
            assertTrue(map.isTreeAtPoint(point));
            map.stepTime();
        }

        /* Verify that the woodcutter stopped cutting */
        assertFalse(woodcutterWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(woodcutterWorker.getCargo());
        assertEquals(woodcutterWorker.getCargo().getMaterial(), WOOD);

        /* Verify that an event was sent when the forester started planting the tree */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(woodcutterWorker)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(woodcutterWorker);

                assertEquals(workerAction, WorkerAction.CUTTING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithStartedActions().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenStonemasonGetsStone() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2, StoneType.STONE_1, 7);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), stone.getPosition());
        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason gets stone */
        for (int i = 0; i < 49; i++) {
            assertTrue(stonemason.isGettingStone());
            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason is done getting stone at the correct time */
        map.stepTime();

        assertFalse(stonemason.isGettingStone());
        assertNotNull(stonemason.getCargo());
        assertEquals(stonemason.getCargo().getMaterial(), STONE);

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(stonemason)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(stonemason);

                assertEquals(workerAction, WorkerAction.HACKING_STONE);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenStonemasonGetsStoneIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        /* Place quarry */
        Point point1 = new Point(10, 4);
        Quarry quarry = map.placeBuilding(new Quarry(player0), point1);

        /* Place stone */
        Point point2 = new Point(11, 5);
        Stone stone = map.placeStone(point2, StoneType.STONE_1, 7);

        /* Construct the quarry */
        constructHouse(quarry);

        /* Assign a stonemason to the quarry */
        Stonemason stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        /* Wait for the stonemason to rest */
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point2));

        /* Step once to let the stonemason go out to get stone */
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        Point point = stonemason.getTarget();

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the stonemason reach the chosen spot if it isn't already there */
        if (!stonemason.isArrived()) {
            Utils.fastForwardUntilWorkersReachTarget(map, stonemason);
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), stone.getPosition());
        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason gets stone */
        for (int i = 0; i < 49; i++) {
            assertTrue(stonemason.isGettingStone());
            map.stepTime();
        }

        assertTrue(stonemason.isGettingStone());

        /* Verify that the stonemason is done getting stone at the correct time */
        map.stepTime();

        assertFalse(stonemason.isGettingStone());
        assertNotNull(stonemason.getCargo());
        assertEquals(stonemason.getCargo().getMaterial(), STONE);

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(stonemason)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(stonemason);

                assertEquals(workerAction, WorkerAction.HACKING_STONE);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithStartedActions().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenFarmerPlantsWhenThereAreFreeSpotsAndNothingToHarvest() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Occupy the farm */
        Farmer farmer = Utils.occupyBuilding(new Farmer(player0, map), farm);

        assertTrue(farmer.isInsideBuilding());

        /* Let the farmer rest */
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        /* Step once and make sure the farmer goes out of the farm */
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the farmer reach the spot and start to plant */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);

        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isPlanting());

        Utils.waitForFarmerToPlantCrop(map, farmer);

        /* Verify that the farmer stopped planting and there is a crop */
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
        assertEquals(map.getCropAtPoint(point).getGrowthState(), JUST_PLANTED);

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(farmer)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(farmer);

                assertEquals(workerAction, WorkerAction.PLANTING_WHEAT);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenFarmerPlantsWhenThereAreFreeSpotsAndNothingToHarvestIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Farm farm = map.placeBuilding(new Farm(player0), point3);

        /* Connect the farm with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter.getFlag());

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Occupy the farm */
        Farmer farmer = Utils.occupyBuilding(new Farmer(player0, map), farm);

        assertTrue(farmer.isInsideBuilding());

        /* Let the farmer rest */
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        /* Step once and make sure the farmer goes out of the farm */
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the farmer reach the spot and start to plant */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);

        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isPlanting());

        Utils.waitForFarmerToPlantCrop(map, farmer);

        /* Verify that the farmer stopped planting and there is a crop */
        assertFalse(farmer.isPlanting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());
        assertEquals(map.getCropAtPoint(point).getGrowthState(), JUST_PLANTED);

        /* Verify that an event was sent when the farmer started picking the crop */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(farmer)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(farmer);

                assertEquals(workerAction, WorkerAction.PLANTING_WHEAT);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertFalse(gameChangesList.getWorkersWithStartedActions().containsKey(farmer));
        }
    }

    @Test
    public void testMonitoringEventsWhenFishermanFishes() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        /* Place fishery */
        Point point4 = new Point(10, 4);
        Building fishery = map.placeBuilding(new Fishery(player0), point4);

        /* Connect the fishery with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter.getFlag());

        /* Wait for the fishery to get constructed */
        Utils.waitForBuildingToBeConstructed(fishery);

        /* Wait for the fishery to get occupied */
        Fisherman fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        assertTrue(fisherman.isInsideBuilding());

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Let the fisherman reach the spot and start fishing */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Verify that (only) an event was sent when the fisherman lowered the rod */
        List<WorkerAction> fishermanActions = Utils.getMonitoredWorkerActionsForWorker(fisherman, monitor);

        assertEquals(fishermanActions.size(), 1);
        assertEquals(fishermanActions.get(0), WorkerAction.LOWER_FISHING_ROD);

        /* Let the fisherman lower the rod and verify no other event is sent */
        Utils.fastForward(15, map);

        fishermanActions = Utils.getMonitoredWorkerActionsForWorker(fisherman, monitor);

        assertEquals(fishermanActions.size(), 1);

        /* Verify that the next event is that the fisherman is fishing */
        map.stepTime();

        fishermanActions = Utils.getMonitoredWorkerActionsForWorker(fisherman, monitor);

        assertEquals(fishermanActions.size(), 2);
        assertEquals(fishermanActions.get(0), WorkerAction.LOWER_FISHING_ROD);
        assertEquals(fishermanActions.get(1), WorkerAction.FISHING);

        /* Let the fisherman fish for a while and verify that no other event is sent */
        Utils.fastForward(89, map);

        fishermanActions = Utils.getMonitoredWorkerActionsForWorker(fisherman, monitor);

        assertEquals(fishermanActions.size(), 2);
        assertEquals(fishermanActions.get(0), WorkerAction.LOWER_FISHING_ROD);
        assertEquals(fishermanActions.get(1), WorkerAction.FISHING);

        /* Verify that an event is sent when the fisherman pulls up the fish */
        map.stepTime();

        fishermanActions = Utils.getMonitoredWorkerActionsForWorker(fisherman, monitor);

        assertEquals(fishermanActions.size(), 3);
        assertEquals(fishermanActions.get(0), WorkerAction.LOWER_FISHING_ROD);
        assertEquals(fishermanActions.get(1), WorkerAction.FISHING);
        assertEquals(fishermanActions.get(2), WorkerAction.PULL_UP_FISHING_ROD);
    }

    @Test
    public void testMonitoringEventWhenFishermanFishesIsOnlySentOnce() throws Exception {

        /* Create a single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place fish on one tile */
        Point point2 = new Point(5, 5);

        map.setDetailedVegetationBelow(point2, WATER);

        /* Place headquarters */
        Point point3 = new Point(15, 9);
        map.placeBuilding(new Headquarter(player0), point3);

        Point point4 = new Point(7, 5);
        Building fishermanHut = map.placeBuilding(new Fishery(player0), point4);

        /* Construct the fisherman hut */
        constructHouse(fishermanHut);

        /* Manually place fisherman */
        Fisherman fisherman = new Fisherman(player0, map);

        Utils.occupyBuilding(fisherman, fishermanHut);

        /* Let the fisherman rest */
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Step once and make sure the fisherman goes out of the hut */
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        Point point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        /* Let the fisherman reach the spot and start fishing */
        Utils.fastForwardUntilWorkersReachTarget(map, fisherman);

        assertTrue(fisherman.isArrived());
        assertTrue(fisherman.isAt(point));
        assertTrue(fisherman.isFishing());

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(fisherman)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(fisherman);

                assertEquals(workerAction, WorkerAction.LOWER_FISHING_ROD);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithStartedActions().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenGeologistDoesResearch() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarters and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

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

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(geologist)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(geologist);

                assertEquals(workerAction, WorkerAction.INVESTIGATING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenGeologistDoesResearchIsOnlySentOnce() throws Exception {

        /* Starting new game */
        Player player0 = new Player("Player 0", java.awt.Color.BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 10);
        Flag flag = map.placeFlag(player0, point1);

        /* Connect headquarters and flag */
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        /* Wait for the road to get occupied */
        Utils.fastForward(30, map);

        /* Call geologist from the flag */
        flag.callGeologist();

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

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

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(geologist)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(geologist);

                assertEquals(workerAction, WorkerAction.INVESTIGATING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertFalse(gameChangesList.getWorkersWithStartedActions().containsKey(geologist));
        }
    }

    @Test
    public void testMonitoringEventWhenFarmerHarvestsWhenPossible() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        /* Place crop */
        Crop crop = map.placeCrop(point3.upRight().upRight(), CropType.TYPE_1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Wait for the crop to grow */
        Utils.fastForwardUntilCropIsGrown(crop, map);

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

        assertTrue(farmer.isInsideBuilding());

        /* Wait for the farmer to rest */
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        /* Step once to let the farmer go out to harvest */
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());
        assertEquals(point, crop.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the farmer reach the crop and start harvesting */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);

        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isHarvesting());
        assertFalse(farmer.isPlanting());

        for (int i = 0; i < 19; i++) {
            assertFalse(farmer.isPlanting());
            assertTrue(farmer.isHarvesting());
            map.stepTime();
        }

        assertTrue(farmer.isHarvesting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(farmer)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(farmer);

                assertEquals(workerAction, WorkerAction.HARVESTING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenFarmerHarvestsWhenPossibleIsOnlySentOnce() throws Exception {

        /* Create single player game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        /* Place farm */
        Point point3 = new Point(10, 6);
        Building farm = map.placeBuilding(new Farm(player0), point3);

        /* Place crop */
        Crop crop = map.placeCrop(point3.upRight().upRight(), CropType.TYPE_1);

        /* Finish construction of the farm */
        Utils.constructHouse(farm);

        /* Wait for the crop to grow */
        Utils.fastForwardUntilCropIsGrown(crop, map);

        /* Assign a farmer to the farm */
        Farmer farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

        assertTrue(farmer.isInsideBuilding());

        /* Wait for the farmer to rest */
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        /* Step once to let the farmer go out to harvest */
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        Point point = farmer.getTarget();

        assertTrue(farmer.isTraveling());
        assertEquals(point, crop.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Let the farmer reach the crop and start harvesting */
        Utils.fastForwardUntilWorkersReachTarget(map, farmer);

        assertTrue(farmer.isArrived());
        assertTrue(farmer.isAt(point));
        assertTrue(farmer.isHarvesting());
        assertFalse(farmer.isPlanting());

        for (int i = 0; i < 19; i++) {
            assertFalse(farmer.isPlanting());
            assertTrue(farmer.isHarvesting());
            map.stepTime();
        }

        assertTrue(farmer.isHarvesting());
        assertTrue(map.isCropAtPoint(point));
        assertNull(farmer.getCargo());

        /* Verify that an event was sent when the stonemason started picking the stone */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(farmer)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(farmer);

                assertEquals(workerAction, WorkerAction.HARVESTING);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertEquals(gameChangesList.getWorkersWithStartedActions().size(), 0);
        }
    }

    @Test
    public void testMonitoringEventWhenBuilderHammers() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Adjust inventory so there is material but no builder */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        /* Connect the woodcutter with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for the planned building to get resources delivered */
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);
        assertTrue(woodcutter0.isPlanned());
        assertFalse(woodcutter0.isReady());

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());

        /* Wait for a builder to leave the headquarters */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 1);

        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Verify that the builder is not building while walking to the woodcutter */
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());

        for (int i = 0; i < 5000; i++) {

            if (builder0.isExactlyAtPoint() && builder0.getPosition().equals(woodcutter0.getPosition())) {
                break;
            }

            assertFalse(builder0.isHammering());

            map.stepTime();
        }

        assertEquals(builder0.getPosition(), woodcutter0.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the builder to go out to a chosen location and build the house */
        assertEquals(builder0.getTarget(), woodcutter0.getPosition().downLeft());
        assertEquals(builder0.getPosition(), woodcutter0.getPosition());
        assertFalse(builder0.isHammering());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, woodcutter0.getPosition().downLeft()); // 20 (not taking direct path)

        assertTrue(builder0.isHammering());

        /* Verify that an event was sent when the builder hammered */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(builder0)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(builder0);

                assertEquals(workerAction, WorkerAction.HAMMERING_HOUSE_HIGH_AND_LOW);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);
    }

    @Test
    public void testMonitoringEventWhenBuilderHammersIsOnlySentOnce() throws InvalidUserActionException {

        /* Starting new game */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        /* Place headquarters */
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place woodcutter */
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        assertTrue(woodcutter0.isPlanned());

        /* Adjust inventory so there is material but no builder */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        /* Connect the woodcutter with the headquarters */
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        /* Wait for the planned building to get resources delivered */
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);
        assertTrue(woodcutter0.isPlanned());
        assertFalse(woodcutter0.isReady());

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());

        /* Wait for a builder to leave the headquarters */
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 1);

        Builder builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        /* Verify that the builder is not building while walking to the woodcutter */
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());

        for (int i = 0; i < 5000; i++) {

            if (builder0.isExactlyAtPoint() && builder0.getPosition().equals(woodcutter0.getPosition())) {
                break;
            }

            assertFalse(builder0.isHammering());

            map.stepTime();
        }

        assertEquals(builder0.getPosition(), woodcutter0.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the builder to go out to a chosen location and build the house */
        assertEquals(builder0.getTarget(), woodcutter0.getPosition().downLeft());
        assertEquals(builder0.getPosition(), woodcutter0.getPosition());
        assertFalse(builder0.isHammering());

        Utils.fastForwardUntilWorkerReachesPoint(map, builder0, woodcutter0.getPosition().downLeft()); // 20 (not taking direct path)

        assertTrue(builder0.isHammering());

        /* Verify that an event was sent when the builder hammered */
        boolean foundEvent = false;
        for (GameChangesList gameChangesList : monitor.getEvents()) {
            if (gameChangesList.getWorkersWithStartedActions().containsKey(builder0)) {
                WorkerAction workerAction = gameChangesList.getWorkersWithStartedActions().get(builder0);

                assertEquals(workerAction, WorkerAction.HAMMERING_HOUSE_HIGH_AND_LOW);

                foundEvent = true;

                break;
            }
        }

        assertTrue(foundEvent);

        /* Verify that the event is only sent once */
        GameChangesList lastEvent = monitor.getEvents().get(monitor.getEvents().size() - 1);

        Utils.fastForward(5, map);

        for (GameChangesList gameChangesList : monitor.getEventsAfterEvent(lastEvent)) {
            assertFalse(gameChangesList.getWorkersWithStartedActions().containsKey(builder0));
        }
    }

    @Test
    public void testMonitoringEventWhenCourierChewsGum() throws InvalidUserActionException {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 27);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 26);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Make sure to get a fat courier */
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            /* Place road */
            Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            /* Wait for a courier to get assigned to the road */
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == FAT) {
                break;
            }

            /* Remove the road */
            map.removeRoad(road);
        }

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to start chewing gum */
        Utils.waitForCourierToChewGum(courier, map);

        /* Verify that an event was sent when the builder hammered */
        int chewGumCount = Utils.countMonitoredWorkerActionForWorker(courier, CHEW_GUM, monitor);

        assertEquals(chewGumCount, 1);
    }

    @Test
    public void testMonitoringEventWhenCourierChewsGumIsOnlySentOnce() throws InvalidUserActionException {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 27);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 26);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Make sure to get a fat courier */
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            /* Place road */
            Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            /* Wait for a courier to get assigned to the road */
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == FAT) {
                break;
            }

            /* Remove the road */
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), FAT);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to start chewing gum */
        Utils.waitForCourierToChewGum(courier, map);

        /* Verify that an event was sent when the builder hammered */
        int chewGumCount = Utils.countMonitoredWorkerActionForWorker(courier, CHEW_GUM, monitor);

        assertEquals(chewGumCount, 1);

        /* Verify that the event is only sent once */
        Utils.fastForward(5, map);

        chewGumCount = Utils.countMonitoredWorkerActionForWorker(courier, CHEW_GUM, monitor);

        assertEquals(chewGumCount, 1);
    }

    @Test
    public void testMonitoringEventWhenCourierCanReadThePaperWhileBored() throws InvalidUserActionException {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 27);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 26);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Make sure to get a thin courier */
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            /* Place road */
            Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            /* Wait for a courier to get assigned to the road */
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            /* Remove the road */
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to read the paper */
        Utils.waitForCourierToReadPaper(courier, map);

        /* Verify that an event was sent when the builder hammered */
        int countReadNewspaper = Utils.countMonitoredWorkerActionForWorker(courier, READ_NEWSPAPER, monitor);

        assertEquals(countReadNewspaper, 1);
    }

    @Test
    public void testMonitoringEventWhenCourierReadsThePaperWhileBoredIsOnlySentOnce() throws InvalidUserActionException {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 27);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 26);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Make sure to get a thin courier */
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            /* Place road */
            Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            /* Wait for a courier to get assigned to the road */
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            /* Remove the road */
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to read the paper */
        Utils.waitForCourierToReadPaper(courier, map);

        /* Verify that an event was sent when the builder hammered */
        int actionCount = Utils.countMonitoredWorkerActionForWorker(courier, READ_NEWSPAPER, monitor);

        assertEquals(actionCount, 1);

        /* Verify that the event is only sent once */
        Utils.fastForward(5, map);

        actionCount = Utils.countMonitoredWorkerActionForWorker(courier, READ_NEWSPAPER, monitor);

        assertEquals(actionCount, 1);
    }

    @Test
    public void testMonitoringEventWhenCourierTouchesNoseWhileBored() throws InvalidUserActionException {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 27);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 26);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Make sure to get a thin courier */
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            /* Place road */
            Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            /* Wait for a courier to get assigned to the road */
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            /* Remove the road */
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to touch the nose while being bored */
        Utils.waitForCourierToTouchNose(courier, map);

        /* Verify that an event was sent when the courier touched the nose */
        int touchNoseCount = Utils.countMonitoredWorkerActionForWorker(courier, WorkerAction.TOUCH_NOSE, monitor);

        assertEquals(touchNoseCount, 1);
    }

    @Test
    public void testMonitoringEventWhenCourierTouchesNoseWhileBoredIsOnlySentOnce() throws InvalidUserActionException {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 27);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 26);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Make sure to get a thin courier */
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            /* Place road */
            Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            /* Wait for a courier to get assigned to the road */
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            /* Remove the road */
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to touch the nose while being bored */
        Utils.waitForCourierToTouchNose(courier, map);

        /* Verify that an event was sent when the courier touched the nose */
        int touchNoseCount = Utils.countMonitoredWorkerActionForWorker(courier, WorkerAction.TOUCH_NOSE, monitor);

        assertEquals(touchNoseCount, 1);

        /* Verify that the event is only sent once */
        Utils.fastForward(5, map);

        touchNoseCount = Utils.countMonitoredWorkerActionForWorker(courier, WorkerAction.TOUCH_NOSE, monitor);

        assertEquals(touchNoseCount, 1);
    }

    @Test
    public void testCourierJumpSkipRopeWhileBored() throws InvalidUserActionException {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 27);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 26);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Make sure to get a thin courier */
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            /* Place road */
            Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            /* Wait for a courier to get assigned to the road */
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            /* Remove the road */
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to jump skip rope */
        Utils.waitForCourierToJumpSkipRope(courier, map);

        /* Verify that an event was sent when the courier touched the nose */
        int jumpSkipRopeCount = Utils.countMonitoredWorkerActionForWorker(courier, WorkerAction.JUMP_SKIP_ROPE, monitor);

        assertEquals(jumpSkipRopeCount, 1);
    }

    @Test
    public void testCourierJumpSkipRopeWhileBoredIsOnlySentOnce() throws InvalidUserActionException {

        /* Creating new game map with size 40x40 */
        Player player0 = new Player("Player 0", BLUE);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 100, 100);

        /* Place headquarters */
        Point point0 = new Point(5, 27);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        /* Place flag */
        Point point1 = new Point(10, 26);
        Flag flag0 = map.placeFlag(player0, point1);

        /* Make sure to get a thin courier */
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            /* Place road */
            Road road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            /* Wait for a courier to get assigned to the road */
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            /* Remove the road */
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for the courier to jump skip rope */
        Utils.waitForCourierToJumpSkipRope(courier, map);

        /* Verify that an event was sent when the courier touched the nose */
        int jumpSkipRopeCount = Utils.countMonitoredWorkerActionForWorker(courier, WorkerAction.JUMP_SKIP_ROPE, monitor);

        assertEquals(jumpSkipRopeCount, 1);

        /* Verify that the event is only sent once */
        Utils.fastForward(5, map);

        jumpSkipRopeCount = Utils.countMonitoredWorkerActionForWorker(courier, WorkerAction.JUMP_SKIP_ROPE, monitor);

        assertEquals(jumpSkipRopeCount, 1);
    }

    @Test
    public void testMonitoringEventWhenHitting() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Remove all soldiers from the headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for one of the soldiers to attack */
        Utils.waitForOneOfSoldiersToHit(map, attacker, defender);

        /* Verify that an event was sent when there was an attack */
        int hitCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.HIT, monitor);
        hitCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.HIT, monitor);

        assertEquals(hitCount, 1);
    }

    @Test
    public void testMonitoringEventWhenHittingIsOnlySentOnce() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Remove all soldiers from the headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for one of the soldiers to attack */
        Utils.waitForOneOfSoldiersToHit(map, attacker, defender);

        /* Verify that an event was sent when there was an attack */
        int hitCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.HIT, monitor);
        hitCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.HIT, monitor);

        assertEquals(hitCount, 1);

        /* Verify that the event is only sent once */
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor
                .getEvents()
                .stream()
                .noneMatch(gameChangesList -> gameChangesList
                        .getWorkersWithStartedActions()
                        .values()
                        .stream()
                        .anyMatch(action -> action == WorkerAction.HIT)));
    }

    @Test
    public void testMonitoringEventWhenSoldierJumpsBack() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Keep attacking until a soldier stands aside during a fight */
        boolean jumpBackFound = false;

        for (int i = 0; i < 20; i++) {

            /* Order an attack */
            assertTrue(player0.canAttack(barracks1));

            player0.attack(barracks1, 1, AttackStrength.STRONG);

            /* Find the military that was chosen to attack */
            map.stepTime();

            Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

            assertNotNull(attacker);
            assertEquals(attacker.getPlayer(), player0);
            assertFalse(attacker.isFighting());

            /* Wait for the military to reach the attacked building */
            assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
            assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

            assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
            assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

            /* Wait for a defender to come out */
            Soldier defender = Utils.findSoldierOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), attacker.getPosition());
            assertFalse(defender.isFighting());

            /* Add a new soldier to the attacked barracks */
            Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

            /* Wait for the fight to start */
            Utils.waitForFightToStart(map, attacker, defender);

            /* Find out if a soldier stands aside during the fight */
            Soldier soldierJumpingBack = null;

            for (int j = 0; j < 10000; j++) {
                if (attacker.isJumpingBack()) {
                    soldierJumpingBack = attacker;

                    break;
                }

                if (defender.isJumpingBack()) {
                    soldierJumpingBack = defender;

                    break;
                }

                if (attacker.isDying() || defender.isDying()) {
                    break;
                }

                map.stepTime();
            }

            /* If a soldier stood aside, verify that an event was sent correctly */
            if (soldierJumpingBack != null) {

                jumpBackFound = true;

                /* Verify that an event was sent when one of the soldiers jumped back */
                int standAsideCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.JUMP_BACK, monitor);
                standAsideCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.JUMP_BACK, monitor);

                assertEquals(standAsideCount, 1);

                break;
            }

            /* Finish the fight if it's not done yet */
            if (!attacker.isDying() && !defender.isDying()) {
                Utils.waitForFightToEnd(map, attacker, defender);
            }

            /* Handle the case where the attacker died */
            if (attacker.isDying()) {

                /* Add a soldier to make it possible to attack again */
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

                /* Wait for the defender to go back to the attacked barracks */
                Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());
            }

            /* Handle the case where the defender died */
            if (defender.isDying()) {

                /* Wait for another soldier to come out and beat the attacker */
                Soldier otherDefender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

                /* Wait for the fight to start */
                Utils.waitForFightToStart(map, attacker, otherDefender);

                /* Wait for the defender to beat the attacker */
                Utils.waitForSoldierToWinFight(otherDefender, map);

                /* Wait for the defender to go back to the barracks */
                Utils.fastForwardUntilWorkerReachesPoint(map, otherDefender, barracks1.getPosition());
            }
        }

        /* Verify that a soldier did stand aside */
        assertTrue(jumpBackFound);
    }

    @Test
    public void testMonitoringEventWhenSoldierJumpsBackIsOnlySentOnce() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks1);

        assertEquals(barracks1.getHostedSoldiers().size(), 2);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Keep attacking until a soldier stands aside during a fight */
        boolean jumpBackFound = false;

        for (int i = 0; i < 20; i++) {

            System.out.println(i);

            /* Order an attack */
            assertTrue(player0.canAttack(barracks1));

            player0.attack(barracks1, 1, AttackStrength.STRONG);

            /* Find the military that was chosen to attack */
            map.stepTime();

            Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

            assertNotNull(attacker);
            assertEquals(attacker.getPlayer(), player0);
            assertFalse(attacker.isFighting());

            /* Wait for the military to reach the attacked building */
            assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);
            assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

            assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
            assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);

            /* Wait for a defender to come out */
            Soldier defender = Utils.findSoldierOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), attacker.getPosition());
            assertFalse(defender.isFighting());
            assertEquals(barracks1.getHostedSoldiers().size(), 1);

            /* Wait for the fight to start */
            Utils.waitForFightToStart(map, attacker, defender);

            /* Find out if a soldier stands aside during the fight */
            Soldier soldierJumpingBack = null;

            for (int j = 0; j < 10000; j++) {
                if (attacker.isJumpingBack()) {
                    soldierJumpingBack = attacker;

                    break;
                }

                if (defender.isJumpingBack()) {
                    soldierJumpingBack = defender;

                    break;
                }

                if (attacker.isDying() || defender.isDying()) {
                    break;
                }

                map.stepTime();
            }

            /* If a soldier stood aside, verify that an event was sent correctly */
            if (soldierJumpingBack != null) {

                jumpBackFound = true;

                /* Verify that an event was sent when one of the soldiers jumped back */
                int standAsideCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.JUMP_BACK, monitor);
                standAsideCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.JUMP_BACK, monitor);

                assertEquals(standAsideCount, 1);

                break;
            }

            /* Finish the fight if it's not done yet */
            if (!attacker.isDying() && !defender.isDying()) {
                Utils.waitForFightToEnd(map, attacker, defender);
            }

            /* Handle the case where the attacker died */
            if (attacker.isDying()) {

                /* Add a soldier to make it possible to attack again */
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

                /* Wait for the defender to go back to the attacked barracks */
                Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());

                assertEquals(barracks1.getHostedSoldiers().size(), 2);
            }

            /* Handle the case where the defender died */
            if (defender.isDying()) {

                /* Wait for another soldier to come out and beat the attacker */
                Soldier otherDefender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

                /* Wait for the fight to start */
                Utils.waitForFightToStart(map, attacker, otherDefender);

                /* Wait for the defender to beat the attacker */
                Utils.waitForSoldierToWinFight(otherDefender, map);

                /* Wait for the defender to go back to the barracks */
                Utils.fastForwardUntilWorkerReachesPoint(map, otherDefender, barracks1.getPosition());

                assertEquals(barracks1.getHostedSoldiers().size(), 2);
            }
        }

        /* Verify that a soldier did stand aside */
        assertTrue(jumpBackFound);

        /* Verify that the event is only sent once */
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor
                .getEvents()
                .stream()
                .noneMatch(gameChangesList -> gameChangesList
                        .getWorkersWithStartedActions()
                        .values()
                        .stream()
                        .anyMatch(action -> action == WorkerAction.JUMP_BACK)));
    }

    @Test
    public void testMonitoringEventWhenSoldierStandsAside() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Keep attacking until a soldier stands aside during a fight */
        boolean standAsideFound = false;

        for (int i = 0; i < 20; i++) {

            /* Order an attack */
            assertTrue(player0.canAttack(barracks1));

            player0.attack(barracks1, 1, AttackStrength.STRONG);

            /* Find the military that was chosen to attack */
            map.stepTime();

            Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

            assertNotNull(attacker);
            assertEquals(attacker.getPlayer(), player0);
            assertFalse(attacker.isFighting());

            /* Wait for the military to reach the attacked building */
            assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

            assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

            /* Wait for a defender to come out */
            Soldier defender = Utils.findSoldierOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), attacker.getPosition());
            assertFalse(defender.isFighting());

            /* Wait for the fight to start */
            Utils.waitForFightToStart(map, attacker, defender);

            /* Find out if a soldier stands aside during the fight */
            Soldier soldierStandingAside = null;

            for (int j = 0; j < 10000; j++) {
                if (attacker.isStandingAside()) {
                    soldierStandingAside = attacker;

                    break;
                }

                if (defender.isStandingAside()) {
                    soldierStandingAside = defender;

                    break;
                }

                if (attacker.isDying() || defender.isDying()) {
                    break;
                }

                map.stepTime();
            }

            /* If a soldier stood aside, verify that an event was sent correctly */
            if (soldierStandingAside != null) {

                standAsideFound = true;

                /* Verify that an event was sent when one of the soldiers jumped back */
                int standAsideCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.STAND_ASIDE, monitor);
                standAsideCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.STAND_ASIDE, monitor);

                assertEquals(standAsideCount, 1);

                break;
            }

            /* Finish the fight if it's not done yet */
            if (!attacker.isDying() && !defender.isDying()) {
                Utils.waitForFightToEnd(map, attacker, defender);
            }

            /* Handle the case where the attacker died */
            if (attacker.isDying()) {

                /* Add a soldier to make it possible to attack again */
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

                /* Wait for the defender to go back to the attacked barracks */
                Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());
            }

            /* Handle the case where the defender died */
            if (defender.isDying()) {

                /* Add a new soldier to the attacked barracks */
                Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

                /* Wait for another soldier to come out and beat the attacker */
                Soldier otherDefender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

                /* Wait for the fight to start */
                Utils.waitForFightToStart(map, attacker, otherDefender);

                /* Wait for the defender to beat the attacker */
                Utils.waitForSoldierToWinFight(otherDefender, map);

                /* Wait for the defender to go back to the barracks */
                Utils.fastForwardUntilWorkerReachesPoint(map, otherDefender, barracks1.getPosition());
            }
        }

        /* Verify that a soldier did stand aside */
        assertTrue(standAsideFound);
    }

    @Test
    public void testMonitoringEventWhenSoldierStandsAsideIsOnlySentOnce() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Clear soldiers from the inventory */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks1);

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Keep attacking until a soldier stands aside during a fight */
        boolean standAsideFound = false;

        for (int i = 0; i < 50; i++) {

            /* Order an attack */
            assertTrue(player0.canAttack(barracks1));

            player0.attack(barracks1, 1, AttackStrength.STRONG);

            /* Find the military that was chosen to attack */
            map.stepTime();

            Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

            assertNotNull(attacker);
            assertEquals(attacker.getPlayer(), player0);
            assertFalse(attacker.isFighting());

            /* Wait for the military to reach the attacked building */
            assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);
            assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

            assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
            assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);

            /* Wait for a defender to come out */
            Soldier defender = Utils.findSoldierOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), attacker.getPosition());
            assertFalse(defender.isFighting());
            assertFalse(attacker.isFighting());
            assertNotEquals(defender, attacker);

            /* Wait for the fight to start */
            Utils.waitForFightToStart(map, attacker, defender);

            /* Find out if a soldier stands aside during the fight */
            for (int j = 0; j < 2000; j++) {
                if (!attacker.isStandingAside() && !defender.isStandingAside()) {
                    break;
                }

                map.stepTime();
            }

            assertFalse(defender.isStandingAside());
            assertFalse(attacker.isStandingAside());

            Soldier soldierStandingAside = null;

            monitor.clearEvents();

            for (int j = 0; j < 20000; j++) {
                if (attacker.isStandingAside()) {
                    soldierStandingAside = attacker;

                    assertFalse(defender.isStandingAside());

                    break;
                }

                if (defender.isStandingAside()) {
                    soldierStandingAside = defender;

                    assertFalse(attacker.isStandingAside());

                    break;
                }

                if (attacker.isDying() || defender.isDying()) {
                    break;
                }

                assertEquals(Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.STAND_ASIDE, monitor), 0);
                assertEquals(Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.STAND_ASIDE, monitor), 0);

                map.stepTime();
            }

            /* If a soldier stood aside, verify that an event was sent correctly */
            if (soldierStandingAside != null) {
                standAsideFound = true;

                assertTrue(player0.getDiscoveredLand().contains(attacker.getPosition()));
                assertTrue(player0.getDiscoveredLand().contains(defender.getPosition()));

                /* Verify that an event was sent when one of the soldiers jumped back */
                int standAsideCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.STAND_ASIDE, monitor);
                standAsideCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.STAND_ASIDE, monitor);

                assertTrue(Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.STAND_ASIDE, monitor) <= 1);
                assertTrue(Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.STAND_ASIDE, monitor) <= 1);
                assertEquals(standAsideCount, 1);

                break;
            }

            /* Finish the fight if it's not done yet */
            if (!attacker.isDying() && !defender.isDying()) {
                Utils.waitForFightToEnd(map, attacker, defender);
            }

            /* Handle the case where the attacker died */
            if (attacker.isDying()) {

                /* Add a soldier to make it possible to attack again */
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

                /* Wait for the defender to go back to the attacked barracks */
                Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());
            }

            /* Handle the case where the defender died */
            if (defender.isDying()) {

                /* Add a new soldier to the attacked barracks */
                Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

                /* Wait for another soldier to come out and beat the attacker */
                Soldier otherDefender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

                /* Wait for the fight to start */
                Utils.waitForFightToStart(map, attacker, otherDefender);

                /* Wait for the defender to beat the attacker */
                Utils.waitForSoldierToWinFight(otherDefender, map);

                /* Wait for the defender to go back to the barracks */
                Utils.fastForwardUntilWorkerReachesPoint(map, otherDefender, barracks1.getPosition());

                assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);
            }
        }

        /* Verify that a soldier did stand aside */
        assertTrue(standAsideFound);

        /* Verify that the event is only sent once */
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor
                .getEvents()
                .stream()
                .noneMatch(gameChangesList -> gameChangesList
                        .getWorkersWithStartedActions()
                        .values()
                        .stream()
                        .anyMatch(action -> action == WorkerAction.STAND_ASIDE)));
    }

    @Test
    public void testMonitoringEventWhenSoldierGetsHit() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Remove all soldiers from the headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);

        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for one of the soldiers to jump back */
        Utils.waitForOneOfSoldiersToGetHit(map, attacker, defender);

        /* Verify that an event was sent when one of the soldiers jumped back */
        int jumpBackCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.GET_HIT, monitor);
        jumpBackCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.GET_HIT, monitor);

        assertEquals(jumpBackCount, 1);
    }

    @Test
    public void testMonitoringEventWhenSoldierGetsHitIsOnlySentOnce() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Remove all soldiers from the headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for one of the soldiers to jump back */
        Utils.waitForOneOfSoldiersToGetHit(map, attacker, defender);

        /* Verify that an event was sent when one of the soldiers jumped back */
        int jumpBackCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.GET_HIT, monitor);
        jumpBackCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.GET_HIT, monitor);

        assertEquals(jumpBackCount, 1);

        /* Verify that the event is only sent once */
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor
                .getEvents()
                .stream()
                .noneMatch(gameChangesList -> gameChangesList
                        .getWorkersWithStartedActions()
                        .values()
                        .stream()
                        .anyMatch(action -> action == WorkerAction.GET_HIT)));
    }

    @Test
    public void testMonitoringEventWhenSoldierIsDying() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Remove all soldiers from the headquarters */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for one of the soldiers to jump back */
        Utils.waitForSoldierToBeDying(defender, map);

        /* Verify that an event was sent when the soldier is dying */
        int dieCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.DIE, monitor);
        dieCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.DIE, monitor);

        assertEquals(dieCount, 1);
    }

    @Test
    public void testMonitoringEventWhenSoldierIsDyingIsOnlySentOnce() throws Exception {

        /* Create player list with two players */
        Player player0 = new Player("Player 0", BLUE);
        Player player1 = new Player("Player 1", GREEN);

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

        /* Clear soldiers from the inventories */
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        /* Place barracks for player 0 */
        Point point2 = new Point(21, 5);
        Building barracks0 = map.placeBuilding(new Barracks(player0), point2);

        /* Place barracks for player 1 */
        Point point3 = new Point(23, 15);
        Building barracks1 = map.placeBuilding(new Barracks(player1), point3);

        /* Finish construction */
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        /* Populate player 0's barracks */
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks0);

        /* Populate player 1's barracks */
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        /* Order an attack */
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        /* Find the military that was chosen to attack */
        map.stepTime();

        Soldier attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        /* Wait for the military to reach the attacked building */
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        /* Wait for the defender to go to the attacker */
        Soldier defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        /* Set up monitoring subscription for the player */
        Utils.GameViewMonitor monitor = new Utils.GameViewMonitor();
        player0.monitorGameView(monitor);

        /* Wait for one of the soldiers to jump back */
        Utils.waitForSoldierToBeDying(defender, map);

        /* Verify that an event was sent when the soldier is dying */
        int dieCount = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.DIE, monitor);
        dieCount += Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.DIE, monitor);

        assertEquals(dieCount, 1);

        /* Verify that the event is only sent once */
        monitor.clearEvents();

        map.stepTime();

        assertTrue(monitor
                .getEvents()
                .stream()
                .noneMatch(gameChangesList -> gameChangesList
                        .getWorkersWithStartedActions()
                        .values()
                        .stream()
                        .anyMatch(action -> action == WorkerAction.GET_HIT)));
    }}
