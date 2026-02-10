package org.appland.settlers.test.monitoring;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.Crop;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.WorkerAction;
import org.appland.settlers.model.actors.Baker;
import org.appland.settlers.model.actors.Builder;
import org.appland.settlers.model.actors.Butcher;
import org.appland.settlers.model.actors.Carpenter;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.Farmer;
import org.appland.settlers.model.actors.Fisherman;
import org.appland.settlers.model.actors.Forester;
import org.appland.settlers.model.actors.Geologist;
import org.appland.settlers.model.actors.Metalworker;
import org.appland.settlers.model.actors.Soldier;
import org.appland.settlers.model.actors.Stonemason;
import org.appland.settlers.model.actors.WellWorker;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.buildings.Bakery;
import org.appland.settlers.model.buildings.Barracks;
import org.appland.settlers.model.buildings.Farm;
import org.appland.settlers.model.buildings.Fishery;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Metalworks;
import org.appland.settlers.model.buildings.Quarry;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.SlaughterHouse;
import org.appland.settlers.model.buildings.Well;
import org.appland.settlers.model.buildings.Woodcutter;
import org.appland.settlers.test.Utils;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.appland.settlers.model.DecorationType.TREE_STUB;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Vegetation.WATER;
import static org.appland.settlers.model.WorkerAction.*;
import static org.appland.settlers.model.actors.Courier.BodyType.FAT;
import static org.appland.settlers.model.actors.Courier.BodyType.THIN;
import static org.appland.settlers.model.actors.Soldier.Rank.GENERAL_RANK;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

public class TestGameMonitoringOfWorkerActions {

    @Test
    public void testMonitoringEventWhenForesterPlantsTree() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(15, 9);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place forester hut
        var point3 = new Point(10, 4);
        var foresterHut = map.placeBuilding(new ForesterHut(player0), point3);

        // Construct the forester hut
        constructHouse(foresterHut);

        // Manually place forester
        var forester = Utils.occupyBuilding(new Forester(player0, map), foresterHut);

        // Let the forester rest
        Utils.fastForward(99, map);

        assertTrue(forester.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(forester.isInsideBuilding());

        var point = forester.getTarget();

        assertTrue(forester.isTraveling());

        // Set up monitoring subscription for the players
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the forester starts planting the tree
        for (int i = 0; i < 2_000; i++) {
            if (forester.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(forester.isArrived());
        assertTrue(forester.isAt(point));
        assertTrue(forester.isPlanting());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(forester), PLANTING_TREE);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(forester), PLANTING_TREE);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(forester));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(forester));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(forester));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(forester));
    }

    @Test
    public void testMonitoringEventWhenWoodcutterCutsDownTree() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(10, 10);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place and grow the tree
        var point3 = new Point(12, 4);
        var tree = map.placeTree(point3, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place the woodcutter
        var point4 = new Point(10, 4);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point4);

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        var woodcutterWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(woodcutterWorker, woodcutter);

        // Wait for the woodcutter to rest
        Utils.fastForward(99, map);

        assertTrue(woodcutterWorker.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());

        var point = woodcutterWorker.getTarget();

        assertEquals(point, point3);
        assertTrue(woodcutterWorker.isTraveling());

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Let the woodcutter reach the tree and start cutting
        for (int i = 0; i < 2_000; i++) {
            if (woodcutterWorker.isCuttingTree()) {
                break;
            }

            monitor0.clearEvents();
            monitor1.clearEvents();
            monitor2.clearEvents();

            map.stepTime();
        }

        assertTrue(woodcutterWorker.isCuttingTree());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(woodcutterWorker), CUTTING);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(woodcutterWorker), CUTTING);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(woodcutterWorker));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(woodcutterWorker));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(woodcutterWorker));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(woodcutterWorker));

        // Verify that an event is sent when the cutting is done and the tree falls down
        for (int i = 0; i < 2_000; i++) {
            if (!woodcutterWorker.isCuttingTree()) {
                break;
            }

            assertFalse(tree.isFalling());

            map.stepTime();
        }

        assertTrue(tree.isFalling());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().newFallingTrees().contains(tree));
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().newFallingTrees().contains(tree));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().newFallingTrees().contains(tree));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().newFallingTrees().contains(tree));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().newFallingTrees().contains(tree));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().newFallingTrees().contains(tree));

        // Verify that an event is sent when the tree is removed after falling
        for (int i = 0; i < 2_000; i++) {
            if (!map.isTreeAtPoint(tree.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertFalse(map.isTreeAtPoint(tree.getPosition()));
        assertFalse(map.isTreeAtPoint(tree.getPosition()));
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().removedTrees().contains(tree));
        assertEquals(monitor0.getLastEvent().newDecorations().get(tree.getPosition()), TREE_STUB);
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().removedTrees().contains(tree));
        assertEquals(monitor1.getLastEvent().newDecorations().get(tree.getPosition()), TREE_STUB);
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().removedTrees().contains(tree) && !monitor2.getLastEvent().newDecorations().containsKey(tree.getPosition())));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() ||
                (!monitor0.getLastEvent().removedTrees().contains(tree) && !monitor0.getLastEvent().newDecorations().containsKey(tree.getPosition())));
        assertTrue(monitor1.getEvents().isEmpty() ||
                (!monitor1.getLastEvent().removedTrees().contains(tree) && !monitor1.getLastEvent().newDecorations().containsKey(tree.getPosition())));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().removedTrees().contains(tree) && !monitor2.getLastEvent().newDecorations().containsKey(tree.getPosition())));
    }

    @Test
    public void testMonitoringEventWhenStonemasonGetsStone() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(10, 10);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place quarry
        var point3 = new Point(10, 4);
        var quarry = map.placeBuilding(new Quarry(player0), point3);

        // Place stone
        var point4 = new Point(11, 5);
        var stone = map.placeStone(point4, Stone.StoneType.STONE_1, 7);

        // Remove all but the last piece of stone
        Utils.removeStonePieces(stone, 1);

        // Construct the quarry
        constructHouse(quarry);

        // Assign a stonemason to the quarry
        var stonemason = new Stonemason(player0, map);

        Utils.occupyBuilding(stonemason, quarry);

        // Wait for the stonemason to rest
        Utils.fastForward(99, map);

        assertTrue(stonemason.isInsideBuilding());
        assertTrue(map.isStoneAtPoint(point4));

        // Step once to let the stonemason go out to get stone
        map.stepTime();

        assertFalse(stonemason.isInsideBuilding());

        assertEquals(stonemason.getTarget(), stone.getPosition());
        assertTrue(stonemason.isTraveling());

        map.stepTime();

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the stonemason gets stone
        for (int i = 0; i < 2_000; i++) {
            if (stonemason.isGettingStone()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(stonemason.isArrived());
        assertEquals(stonemason.getPosition(), stone.getPosition());
        assertTrue(stonemason.isGettingStone());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(stonemason), HACKING_STONE);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(stonemason), HACKING_STONE);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(stonemason));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(stonemason));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(stonemason));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(stonemason));

        // Verify that an event is sent when the stone is removed and the stonemason is going back
        for (int i = 0; i < 2_000; i++) {
            if (!stonemason.isGettingStone()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(stonemason.isGettingStone());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().removedStones().contains(stone));
        assertTrue(monitor0.getLastEvent().workersWithNewTargets().contains(stonemason));
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().removedStones().contains(stone));
        assertTrue(monitor1.getLastEvent().workersWithNewTargets().contains(stonemason));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().removedStones().contains(stone) && !monitor2.getLastEvent().workersWithNewTargets().contains(stonemason)));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() ||
                (!monitor0.getLastEvent().removedStones().contains(stone) && !monitor0.getLastEvent().workersWithNewTargets().contains(stonemason)));
        assertTrue(monitor1.getEvents().isEmpty() ||
                (!monitor1.getLastEvent().removedStones().contains(stone) && !monitor1.getLastEvent().workersWithNewTargets().contains(stonemason)));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().removedStones().contains(stone) && !monitor2.getLastEvent().workersWithNewTargets().contains(stonemason)));
    }

    @Test
    public void testMonitoringEventWhenFarmerPlants() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(10, 10);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place farm
        var point3 = new Point(10, 6);
        var farm = map.placeBuilding(new Farm(player0), point3);

        // Connect the farm with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, farm.getFlag(), headquarter0.getFlag());

        // Finish construction of the farm
        Utils.constructHouse(farm);

        // Occupy the farm
        var farmer = Utils.occupyBuilding(new Farmer(player0, map), farm);

        assertTrue(farmer.isInsideBuilding());

        // Let the farmer rest
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        // Step once and make sure the farmer goes out of the farm
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        assertTrue(farmer.isTraveling());

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the farmer starts planting
        for (int i = 0; i < 2_000; i++) {
            if (farmer.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(farmer.isPlanting());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(farmer), PLANTING_WHEAT);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(farmer), PLANTING_WHEAT);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(farmer));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(farmer));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(farmer));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(farmer));

        // Verify that an event is sent when the farmer is done planting and there is a newly planted crop
        for (int i = 0; i < 2_000; i++) {
            if (!farmer.isPlanting()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(farmer.isPlanting());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().newCrops().contains(map.getCropAtPoint(farmer.getPosition())));
        assertTrue(monitor0.getLastEvent().workersWithNewTargets().contains(farmer));
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().newCrops().contains(map.getCropAtPoint(farmer.getPosition())));
        assertTrue(monitor1.getLastEvent().workersWithNewTargets().contains(farmer));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().workersWithNewTargets().contains(farmer) &&
                    !monitor2.getLastEvent().newCrops().contains(map.getCropAtPoint(farmer.getPosition()))));
    }

    @Test
    public void testMonitoringEventsWhenFishermanFishes() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(10, 10);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place fish on one tile
        var point3 = new Point(5, 5);
        map.setVegetationBelow(point3, WATER);

        // Place fishery
        var point4 = new Point(10, 4);
        var fishery = map.placeBuilding(new Fishery(player0), point4);

        // Connect the fishery with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, fishery.getFlag(), headquarter0.getFlag());

        // Wait for the fishery to get constructed
        Utils.waitForBuildingToBeConstructed(fishery);

        // Wait for the fishery to get occupied
        Fisherman fisherman = (Fisherman) Utils.waitForNonMilitaryBuildingToGetPopulated(fishery);

        assertTrue(fisherman.isInsideBuilding());

        // Let the fisherman rest
        Utils.fastForward(99, map);

        assertTrue(fisherman.isInsideBuilding());

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Step once and make sure the fisherman goes out of the hut
        map.stepTime();

        assertFalse(fisherman.isInsideBuilding());

        var point = fisherman.getTarget();

        assertTrue(fisherman.isTraveling());

        // Verify that an event is sent when the fisherman lowers the rod and starts to fish
        for (int i = 0; i < 2_000; i++) {
            if (fisherman.isFishing()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(fisherman.isFishing());
        assertTrue(fisherman.isLoweringFishingRod());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(fisherman), LOWER_FISHING_ROD);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(fisherman), LOWER_FISHING_ROD);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(fisherman));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(fisherman));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(fisherman));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(fisherman));

        // Verify that an event is sent when the fisherman is done lowering the fishing rod
        for (int i = 0; i < 2_000; i++) {
            if (!fisherman.isLoweringFishingRod()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(fisherman.isLoweringFishingRod());
        assertTrue(fisherman.isFishing());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(fisherman), FISHING);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(fisherman), FISHING);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(fisherman));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(fisherman));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(fisherman));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(fisherman));

        // Verify that an event is sent when the fisherman is done fishing and raises the rod
        for (int i = 0; i < 2_000; i++) {
            if (fisherman.isPullingUpFish()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(fisherman.isPullingUpFish());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(fisherman), PULL_UP_FISHING_ROD);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(fisherman), PULL_UP_FISHING_ROD);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(fisherman));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(fisherman));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(fisherman));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(fisherman));

        // Verify that an event is sent when the fisherman is done pulling up the fish and starts going back
        for (int i = 0; i < 2_000; i++) {
            if (!fisherman.isPullingUpFish()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(fisherman.isPullingUpFish());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().workersWithNewTargets().contains(fisherman));
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().workersWithNewTargets().contains(fisherman));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().workersWithNewTargets().contains(fisherman) &&
                    !monitor2.getLastEvent().workersWithStartedActions().containsKey(fisherman)));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() ||
                (!monitor0.getLastEvent().workersWithNewTargets().contains(fisherman) &&
                    !monitor0.getLastEvent().workersWithStartedActions().containsKey(fisherman)));
        assertTrue(monitor1.getEvents().isEmpty() ||
                (!monitor1.getLastEvent().workersWithNewTargets().contains(fisherman) &&
                    !monitor1.getLastEvent().workersWithStartedActions().containsKey(fisherman)));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().workersWithNewTargets().contains(fisherman) &&
                    !monitor2.getLastEvent().workersWithStartedActions().containsKey(fisherman)));
    }

    @Test
    public void testMonitoringEventWhenGeologistDoesResearch() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(10, 10);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place flag
        var point3 = new Point(15, 15);
        var flag = map.placeFlag(player0, point3);

        // Connect headquarters and flag
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag);

        // Wait for the road to get occupied
        Utils.fastForward(30, map);

        // Call geologist from the flag
        flag.callGeologist();

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Wait for the geologist to go to the flag
        map.stepTime();

        Geologist geologist = null;

        for (var worker : map.getWorkers()) {
            if (worker instanceof Geologist) {
                geologist = (Geologist)worker;
            }
        }

        assertNotNull(geologist);
        assertEquals(geologist.getTarget(), flag.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, geologist, geologist.getTarget());

        // Verify that an event is sent when the geologist starts to investigate
        for (int i = 0; i < 2_000; i++) {
            if (geologist.isInvestigating()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(geologist.isInvestigating());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(geologist), INVESTIGATING);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(geologist), INVESTIGATING);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(geologist));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(geologist));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(geologist));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(geologist));
    }

    @Test
    public void testMonitoringEventWhenFarmerHarvests() throws Exception {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(10, 10);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place farm
        var point3 = new Point(10, 6);
        var farm = map.placeBuilding(new Farm(player0), point3);

        // Place crop
        var crop = map.placeCrop(point3.upRight().upRight(), Crop.CropType.TYPE_1);

        // Finish construction of the farm
        Utils.constructHouse(farm);

        // Wait for the crop to grow
        Utils.fastForwardUntilCropIsGrown(crop, map);

        // Assign a farmer to the farm
        var farmer = new Farmer(player0, map);

        Utils.occupyBuilding(farmer, farm);

        assertTrue(farmer.isInsideBuilding());

        // Wait for the farmer to rest
        Utils.fastForward(99, map);

        assertTrue(farmer.isInsideBuilding());

        // Step once to let the farmer go out to harvest
        map.stepTime();

        assertFalse(farmer.isInsideBuilding());

        var point = farmer.getTarget();

        assertTrue(farmer.isTraveling());
        assertEquals(point, crop.getPosition());

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the farmer starts to harvest
        for (int i = 0; i < 2_000; i++) {
            if (farmer.isHarvesting()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(farmer.isHarvesting());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(farmer), HARVESTING);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(farmer), HARVESTING);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(farmer));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(farmer));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(farmer));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(farmer));
    }

    @Test
    public void testMonitoringEventWhenBuilderHammers() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(10, 10);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place woodcutter
        var point3 = new Point(6, 12);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point3);

        assertTrue(woodcutter0.isPlanned());

        // Adjust inventory so there is material but no builder
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.PLANK, 10);
        Utils.adjustInventoryTo(headquarter0, Material.STONE, 10);

        // Connect the var with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        // Wait for the planned building to get resources delivered
        assertEquals(woodcutter0.getAmount(Material.PLANK), 0);
        assertTrue(woodcutter0.isPlanned());
        assertFalse(woodcutter0.isReady());

        Utils.waitForBuildingToGetAmountOfMaterial(woodcutter0, Material.PLANK, 2);

        assertEquals(woodcutter0.getAmount(Material.PLANK), 2);
        assertTrue(woodcutter0.isPlanned());

        // Wait for a builder to leave the headquarters
        Utils.adjustInventoryTo(headquarter0, Material.BUILDER, 1);

        var builder0 = Utils.waitForWorkerOutsideBuilding(Builder.class, player0);

        // Verify that the builder is not building while walking to the woodcutter
        assertEquals(builder0.getTarget(), woodcutter0.getPosition());

        for (int i = 0; i < 5000; i++) {
            if (builder0.isExactlyAtPoint() && builder0.getPosition().equals(woodcutter0.getPosition())) {
                break;
            }

            assertFalse(builder0.isHammering());

            map.stepTime();
        }

        assertEquals(builder0.getPosition(), woodcutter0.getPosition());

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the builder starts to hammer
        for (int i = 0; i < 2_000; i++) {
            if (builder0.isHammering()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(builder0.isHammering());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(builder0), HAMMERING_HOUSE_HIGH_AND_LOW);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(builder0), HAMMERING_HOUSE_HIGH_AND_LOW);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(builder0));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(builder0));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(builder0));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(builder0));
    }

    @Test
    public void testMonitoringEventWhenCourierChewsGum() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(5, 27);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place flag
        var point3 = new Point(10, 26);
        var flag0 = map.placeFlag(player0, point3);

        // Make sure to get a fat courier
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            // Place road
            var road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            // Wait for a courier to get assigned to the road
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == FAT) {
                break;
            }

            // Remove the road
            map.removeRoad(road);
        }

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the courier starts chewing gum
        for (int i = 0; i < 2_000; i++) {
            if (courier.isChewingGum()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isChewingGum());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(courier), CHEW_GUM);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(courier), CHEW_GUM);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(courier));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(courier));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(courier));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(courier));
    }

    @Test
    public void testMonitoringEventWhenCourierCanReadThePaperWhileBored() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(5, 27);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place flag
        var point3 = new Point(10, 26);
        var flag0 = map.placeFlag(player0, point3);

        // Make sure to get a thin courier
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            // Place road
            var road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            // Wait for a courier to get assigned to the road
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            // Remove the road
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the courier reads the paper
        for (int i = 0; i < 2_000; i++) {
            if (courier.isReadingPaper()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isReadingPaper());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(courier), READ_NEWSPAPER);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(courier), READ_NEWSPAPER);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(courier));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(courier));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(courier));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(courier));
    }

    @Test
    public void testMonitoringEventWhenCourierTouchesNoseWhileBored() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(5, 27);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place flag
        var point3 = new Point(10, 26);
        var flag0 = map.placeFlag(player0, point3);

        // Make sure to get a thin courier
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            // Place road
            var road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            // Wait for a courier to get assigned to the road
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            // Remove the road
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the courier touches the nose while being bored
        for (int i = 0; i < 2_000; i++) {
            if (courier.isTouchingNose()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isTouchingNose());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(courier), TOUCH_NOSE);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(courier), TOUCH_NOSE);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(courier));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(courier));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(courier));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(courier));
    }

    @Test
    public void testCourierJumpSkipRopeWhileBored() throws InvalidUserActionException {

        // Create game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(5, 27);
        var point1 = new Point(80, 80);
        var point2 = new Point(10, 80);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        map.placeBuilding(new Headquarter(player1), point1);
        map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full map
        GameUtils.discoverFullMap(player1);

        // Place flag
        var point3 = new Point(10, 26);
        var flag0 = map.placeFlag(player0, point3);

        // Make sure to get a thin courier
        Courier courier = null;

        for (int i = 0; i < 20; i++) {

            // Place road
            var road = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

            // Wait for a courier to get assigned to the road
            courier = Utils.waitForRoadToGetAssignedCourier(map, road);

            if (courier.getBodyType() == THIN) {
                break;
            }

            // Remove the road
            map.removeRoad(road);
        }

        assertEquals(courier.getBodyType(), THIN);

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the courier jumps skip rope
        for (int i = 0; i < 2_000; i++) {
            if (courier.isJumpingSkipRope()) {
                break;
            }

            map.stepTime();
        }

        assertTrue(courier.isJumpingSkipRope());
        assertTrue(monitor0.getEvents().size() > 0);
        System.out.println(monitor0.getLastEvent());
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(courier), JUMP_SKIP_ROPE);
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(courier), JUMP_SKIP_ROPE);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(courier));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(courier));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(courier));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(courier));
    }

    @Test
    public void testMonitoringEventWhenHitting() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Remove all soldiers from the headquarters
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0
        var point3 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point3);

        // Place barracks for player 1
        var point4 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        // Wait for the military to reach the attacked building
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Wait for the defender to go to the attacker
        var defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Wait for one of the soldiers to attack
        Utils.waitForOneOfSoldiersToHit(map, attacker, defender);

        // Verify that an event was sent when there was an attack
        int hitsSeenByPlayer0 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.HIT, monitor0)
                            + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.HIT, monitor0);
        int hitsSeenByPlayer1 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.HIT, monitor1)
                            + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.HIT, monitor1);
        int hitsSeenByPlayer2 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.HIT, monitor2)
                            + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.HIT, monitor2);

        assertEquals(hitsSeenByPlayer0, 1);
        assertEquals(hitsSeenByPlayer1, 1);
        assertEquals(hitsSeenByPlayer2, 0);

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() ||
                (!monitor0.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor0.getLastEvent().workersWithStartedActions().containsKey(defender)));
        assertTrue(monitor1.getEvents().isEmpty() ||
                (!monitor1.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor1.getLastEvent().workersWithStartedActions().containsKey(defender)));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor2.getLastEvent().workersWithStartedActions().containsKey(defender)));
    }

    // TODO: continue adding asserts that all players see the actions happening

    @Test
    public void testMonitoringEventWhenSoldierJumpsBack() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Clear soldiers from the inventory
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0
        var point3 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point3);

        // Place barracks for player 1
        var point4 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks1);

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Keep attacking until a soldier stands aside during a fight
        boolean jumpBackFound = false;

        for (int i = 0; i < 20; i++) {

            // Order an attack
            assertTrue(player0.canAttack(barracks1));

            player0.attack(barracks1, 1, AttackStrength.STRONG);

            // Find the military that was chosen to attack
            map.stepTime();

            var attacker = Utils.findSoldierOutsideBuilding(player0);

            assertNotNull(attacker);
            assertEquals(attacker.getPlayer(), player0);
            assertFalse(attacker.isFighting());

            // Wait for the military to reach the attacked building
            assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);
            assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

            assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
            assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);

            // Wait for a defender to come out
            var defender = Utils.findSoldierOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), attacker.getPosition());
            assertFalse(defender.isFighting());

            // Wait for the fight to start
            Utils.waitForFightToStart(map, attacker, defender);

            // Find out if a soldier stands aside during the fight
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

            // If a soldier stood aside, verify that an event was sent correctly
            if (soldierJumpingBack != null) {
                jumpBackFound = true;

                // Verify that an event was sent when one of the soldiers jumped back
                int jumpBackSeenByPlayer0 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.JUMP_BACK, monitor0)
                        + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.JUMP_BACK, monitor0);
                int jumpBackSeenByPlayer1 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.JUMP_BACK, monitor1)
                        + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.JUMP_BACK, monitor1);
                int jumpBackSeenByPlayer2 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.JUMP_BACK, monitor2)
                        + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.JUMP_BACK, monitor2);

                assertEquals(jumpBackSeenByPlayer0, 1);
                assertEquals(jumpBackSeenByPlayer1, 1);
                assertEquals(jumpBackSeenByPlayer2, 0);

                // Verify that the event is only sent once
                monitor0.clearEvents();
                monitor1.clearEvents();
                monitor2.clearEvents();

                map.stepTime();

                assertTrue(monitor0.getEvents().isEmpty() ||
                        (!monitor0.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor0.getLastEvent().workersWithStartedActions().containsKey(defender)));
                assertTrue(monitor1.getEvents().isEmpty() ||
                        (!monitor1.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor1.getLastEvent().workersWithStartedActions().containsKey(defender)));
                assertTrue(monitor2.getEvents().isEmpty() ||
                        (!monitor2.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor2.getLastEvent().workersWithStartedActions().containsKey(defender)));

                break;
            }

            // Finish the fight if it's not done yet
            if (!attacker.isDying() && !defender.isDying()) {
                Utils.waitForFightToEnd(map, attacker, defender);
            }

            // Handle the case where the attacker died
            if (attacker.isDying()) {

                // Add a soldier to make it possible to attack again
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

                // Wait for the defender to go back to the attacked barracks
                Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());

                assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);
            }

            // Handle the case where the defender died
            if (defender.isDying()) {

                // Wait for another soldier to come out and beat the attacker
                var otherDefender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

                // Wait for the fight to start
                Utils.waitForFightToStart(map, attacker, otherDefender);

                // Wait for the defender to beat the attacker
                Utils.waitForSoldierToWinFight(otherDefender, map);

                // Wait for the defender to go back to the barracks
                Utils.fastForwardUntilWorkerReachesPoint(map, otherDefender, barracks1.getPosition());

                // Add a new soldier to the attacked barracks
                Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

                assertEquals(barracks1.getNumberOfHostedSoldiers(), 2);
            }
        }

        // Verify that a soldier did stand aside
        assertTrue(jumpBackFound);
    }

    @Test
    public void testMonitoringEventWhenSoldierStandsAside() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Clear soldiers from the inventory
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0
        var point3 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point3);

        // Place barracks for player 1
        var point4 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);
        Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Keep attacking until a soldier stands aside during a fight
        boolean standAsideFound = false;

        for (int i = 0; i < 20; i++) {

            // Order an attack
            assertTrue(player0.canAttack(barracks1));

            player0.attack(barracks1, 1, AttackStrength.STRONG);

            // Find the military that was chosen to attack
            map.stepTime();

            var attacker = Utils.findSoldierOutsideBuilding(player0);

            assertNotNull(attacker);
            assertEquals(attacker.getPlayer(), player0);
            assertFalse(attacker.isFighting());

            // Wait for the military to reach the attacked building
            assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

            assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());

            // Wait for a defender to come out
            var defender = Utils.findSoldierOutsideBuilding(player1);

            assertNotNull(defender);
            assertEquals(defender.getTarget(), attacker.getPosition());
            assertFalse(defender.isFighting());

            // Wait for the fight to start
            Utils.waitForFightToStart(map, attacker, defender);

            // Find out if a soldier stands aside during the fight
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

            // If a soldier stood aside, verify that an event was sent correctly
            if (soldierStandingAside != null) {
                standAsideFound = true;

                // Verify that an event was sent when one of the soldiers jumped back
                int standAsideSeenByPlayer0 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.STAND_ASIDE, monitor0)
                                            + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.STAND_ASIDE, monitor0);
                int standAsideSeenByPlayer1 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.STAND_ASIDE, monitor1)
                                            + Utils.countMonitoredWorkerActionForWorker(defender, STAND_ASIDE, monitor1);
                int standAsideSeenByPlayer2 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.STAND_ASIDE, monitor2)
                                            + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.STAND_ASIDE, monitor2);

                assertEquals(standAsideSeenByPlayer0, 1);
                assertEquals(standAsideSeenByPlayer1, 1);
                assertEquals(standAsideSeenByPlayer2, 0);

                // Verify that the event is only sent once
                monitor0.clearEvents();
                monitor1.clearEvents();
                monitor2.clearEvents();

                map.stepTime();

                assertTrue(monitor0.getEvents().isEmpty() ||
                        (!monitor0.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor0.getLastEvent().workersWithStartedActions().containsKey(defender)));
                assertTrue(monitor1.getEvents().isEmpty() ||
                        (!monitor1.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor1.getLastEvent().workersWithStartedActions().containsKey(defender)));
                assertTrue(monitor2.getEvents().isEmpty() ||
                        (!monitor2.getLastEvent().workersWithStartedActions().containsKey(attacker) && !monitor2.getLastEvent().workersWithStartedActions().containsKey(defender)));

                break;
            }

            // Finish the fight if it's not done yet
            if (!attacker.isDying() && !defender.isDying()) {
                Utils.waitForFightToEnd(map, attacker, defender);
            }

            // Handle the case where the attacker died
            if (attacker.isDying()) {

                // Add a soldier to make it possible to attack again
                Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks0);

                // Wait for the defender to go back to the attacked barracks
                Utils.fastForwardUntilWorkerReachesPoint(map, defender, barracks1.getPosition());
            }

            // Handle the case where the defender died
            if (defender.isDying()) {

                // Add a new soldier to the attacked barracks
                Utils.occupyMilitaryBuilding(GENERAL_RANK, barracks1);

                // Wait for another soldier to come out and beat the attacker
                Soldier otherDefender = Utils.waitForSoldierNotDyingOutsideBuilding(player1);

                // Wait for the fight to start
                Utils.waitForFightToStart(map, attacker, otherDefender);

                // Wait for the defender to beat the attacker
                Utils.waitForSoldierToWinFight(otherDefender, map);

                // Wait for the defender to go back to the barracks
                Utils.fastForwardUntilWorkerReachesPoint(map, otherDefender, barracks1.getPosition());
            }
        }

        // Verify that a soldier did stand aside
        assertTrue(standAsideFound);
    }

    @Test
    public void testMonitoringEventWhenSoldierGetsHit() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Remove all soldiers from the headquarters
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0
        var point3 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point3);

        // Place barracks for player 1
        var point4 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, 2, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        // Wait for the military to reach the attacked building
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Wait for the defender to go to the attacker
        var defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);

        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Wait for one of the soldiers to jump back
        Utils.waitForOneOfSoldiersToGetHit(map, attacker, defender);

        // Verify that an event was sent when one of the soldiers jumped back
        int jumpBackSeenByPlayer0 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.GET_HIT, monitor0)
                                    + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.GET_HIT, monitor0);
        int jumpBackSeenByPlayer1 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.GET_HIT, monitor1)
                                    + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.GET_HIT, monitor1);
        int jumpBackSeenByPlayer2 = Utils.countMonitoredWorkerActionForWorker(attacker, GET_HIT, monitor2)
                                    + Utils.countMonitoredWorkerActionForWorker(defender, GET_HIT, monitor2);

        assertEquals(jumpBackSeenByPlayer0, 1);
        assertEquals(jumpBackSeenByPlayer1, 1);
        assertEquals(jumpBackSeenByPlayer2, 0);

        // Verify that the event is sent only once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() ||
                (!monitor0.getLastEvent().workersWithStartedActions().containsKey(attacker) &&
                        !monitor0.getLastEvent().workersWithStartedActions().containsKey(defender)));
        assertTrue(monitor1.getEvents().isEmpty() ||
                (!monitor1.getLastEvent().workersWithStartedActions().containsKey(attacker) &&
                        !monitor1.getLastEvent().workersWithStartedActions().containsKey(defender)));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().workersWithStartedActions().containsKey(attacker) &&
                        !monitor2.getLastEvent().workersWithStartedActions().containsKey(defender)));
    }

    @Test
    public void testMonitoringEventWhenSoldierIsDying() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Remove all soldiers from the headquarters
        Utils.clearInventory(headquarter0, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);
        Utils.clearInventory(headquarter1, PRIVATE, PRIVATE_FIRST_CLASS, SERGEANT, OFFICER, GENERAL);

        // Place barracks for player 0
        var point3 = new Point(21, 5);
        var barracks0 = map.placeBuilding(new Barracks(player0), point3);

        // Place barracks for player 1
        var point4 = new Point(23, 15);
        var barracks1 = map.placeBuilding(new Barracks(player1), point4);

        // Finish construction
        Utils.constructHouse(barracks0);
        Utils.constructHouse(barracks1);

        // Populate player 0's barracks
        Utils.occupyMilitaryBuilding(GENERAL_RANK, 2, barracks0);

        // Populate player 1's barracks
        assertTrue(barracks1.isReady());

        Utils.occupyMilitaryBuilding(PRIVATE_RANK, barracks1);

        // Order an attack
        assertTrue(player0.canAttack(barracks1));

        player0.attack(barracks1, 1, AttackStrength.STRONG);

        // Find the military that was chosen to attack
        map.stepTime();

        var attacker = Utils.findSoldierOutsideBuilding(player0);

        assertNotNull(attacker);
        assertEquals(attacker.getPlayer(), player0);
        assertFalse(attacker.isFighting());

        // Wait for the military to reach the attacked building
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 1);
        assertEquals(attacker.getTarget(), barracks1.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, attacker, barracks1.getFlag().getPosition());

        assertEquals(attacker.getPosition(), barracks1.getFlag().getPosition());
        assertEquals(barracks1.getNumberOfHostedSoldiers(), 0);

        // Wait for the defender to go to the attacker
        var defender = Utils.findSoldierOutsideBuilding(player1);

        assertNotNull(defender);
        assertEquals(defender.getTarget(), attacker.getPosition());
        assertFalse(defender.isFighting());

        Utils.fastForwardUntilWorkerReachesPoint(map, defender, attacker.getPosition());

        assertEquals(defender.getPosition(), attacker.getPosition());

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Wait for one of the soldiers to jump back
        Utils.waitForSoldierToBeDying(defender, map);

        // Verify that an event was sent when the soldier is dying
        int deathsSeenByPlayer0 = Utils.countMonitoredWorkerActionForWorker(attacker, WorkerAction.DIE, monitor0)
                                + Utils.countMonitoredWorkerActionForWorker(defender, WorkerAction.DIE, monitor0);
        int deathsSeenByPlayer1 = Utils.countMonitoredWorkerActionForWorker(attacker, DIE, monitor1)
                                + Utils.countMonitoredWorkerActionForWorker(defender, DIE, monitor1);
        int deathsSeenByPlayer2 = Utils.countMonitoredWorkerActionForWorker(attacker, DIE, monitor2)
                                + Utils.countMonitoredWorkerActionForWorker(defender, DIE, monitor2);

        assertEquals(deathsSeenByPlayer0, 1);
        assertEquals(deathsSeenByPlayer1, 1);
        assertEquals(deathsSeenByPlayer2, 0);

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() ||
                (!monitor0.getLastEvent().workersWithStartedActions().containsKey(attacker) &&
                        !monitor0.getLastEvent().workersWithStartedActions().containsKey(defender)));
        assertTrue(monitor1.getEvents().isEmpty() ||
                (!monitor1.getLastEvent().workersWithStartedActions().containsKey(attacker) &&
                        !monitor1.getLastEvent().workersWithStartedActions().containsKey(defender)));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().workersWithStartedActions().containsKey(attacker) &&
                        !monitor2.getLastEvent().workersWithStartedActions().containsKey(defender)));
    }

    @Test
    public void testMonitoringEventsWhenMetalworkerWorksToProduceTool() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Place metalworks
        var point3 = new Point(7, 9);
        var metalworks = map.placeBuilding(new Metalworks(player0), point3);

        // Finish construction of the metalworks
        Utils.constructHouse(metalworks);

        // Occupy the metalworks
        var metalworker0 = Utils.occupyBuilding(new Metalworker(player0, map), metalworks);

        assertTrue(metalworker0.isInsideBuilding());
        assertEquals(metalworker0.getHome(), metalworks);
        assertEquals(metalworks.getWorker(), metalworker0);

        // Deliver plank and iron bar to the metalworks
        Utils.deliverCargo(metalworks, PLANK);
        Utils.deliverCargo(metalworks, IRON_BAR);

        // Set up monitoring subscription for the player
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Let the metalworker rest
        for (int i = 0; i < 100; i++) {
            assertTrue(metalworker0.isInsideBuilding());
            assertFalse(metalworker0.isHammering());
            assertFalse(metalworker0.isSawing());
            assertFalse(metalworker0.isWipingSweat());
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());

            monitor0.clearEvents();

            map.stepTime();
        }

        // Verify that an event is sent when the metalworker starts to hammer
        assertTrue(metalworker0.isHammering());
        assertFalse(metalworker0.isSawing());
        assertFalse(metalworker0.isWipingSweat());
        assertTrue(monitor0.getEvents().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(metalworker0), HAMMER_TO_MAKE_TOOL);
        assertTrue(monitor0.getLastEvent().newWorkersOutside().contains(metalworker0));
        assertTrue(monitor1.getEvents().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(metalworker0), HAMMER_TO_MAKE_TOOL);
        assertTrue(monitor1.getLastEvent().newWorkersOutside().contains(metalworker0));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(metalworker0));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().newWorkersOutside().contains(metalworker0));
        assertFalse(metalworker0.isInsideBuilding());

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().size() == 0 ||
                monitor0.getLastEvent().workersWithStartedActions().get(metalworker0) != WorkerAction.HAMMER_TO_MAKE_TOOL);
        assertTrue(monitor0.getEvents().size() == 0 || !monitor0.getLastEvent().newWorkersOutside().contains(metalworker0));
        assertTrue(monitor1.getEvents().size() == 0 ||
                monitor1.getLastEvent().workersWithStartedActions().get(metalworker0) != HAMMER_TO_MAKE_TOOL);
        assertTrue(monitor1.getEvents().size() == 0 || !monitor1.getLastEvent().newWorkersOutside().contains(metalworker0));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (monitor2.getLastEvent().workersWithStartedActions().get(metalworker0) != WorkerAction.HAMMER_TO_MAKE_TOOL &&
                        !monitor2.getLastEvent().newWorkersOutside().contains(metalworker0)));

        // Verify than an event is sent when the metalworker starts to saw
        monitor0.clearEvents();

        for (int i = 0; i < 29; i++) {
            assertTrue(metalworker0.isHammering());
            assertFalse(metalworker0.isSawing());
            assertFalse(metalworker0.isWipingSweat());
            assertFalse(metalworker0.isInsideBuilding());
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
            assertTrue(monitor0.getEvents().size() == 0 || !monitor0.getLastEvent().workersWithStartedActions().containsKey(metalworker0));
            assertTrue(monitor1.getEvents().size() == 0 || !monitor1.getLastEvent().workersWithStartedActions().containsKey(metalworker0));
            assertTrue(monitor2.getEvents().size() == 0 || !monitor2.getLastEvent().workersWithStartedActions().containsKey(metalworker0));

            map.stepTime();
        }

        assertFalse(metalworker0.isHammering());
        assertTrue(metalworker0.isSawing());
        assertFalse(metalworker0.isWipingSweat());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().workersWithStartedActions().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(metalworker0), WorkerAction.SAWING_TO_MAKE_TOOL);
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().workersWithStartedActions().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(metalworker0), WorkerAction.SAWING_TO_MAKE_TOOL);
        assertTrue(monitor2.getEvents().isEmpty() ||
            (!monitor2.getLastEvent().workersWithStartedActions().containsKey(metalworker0) &&
                !monitor2.getLastEvent().workersWithStartedActions().containsKey(metalworker0)));

        // Verify that an event is sent when the metalworker starts to wipe sweat
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        for (int i = 0; i < 30; i++) {
            assertFalse(metalworker0.isHammering());
            assertTrue(metalworker0.isSawing());
            assertFalse(metalworker0.isWipingSweat());
            assertFalse(metalworker0.isInsideBuilding());
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
            assertTrue(monitor0.getEvents().size() == 0 || !monitor0.getLastEvent().workersWithStartedActions().containsKey(metalworker0));
            assertTrue(monitor1.getEvents().size() == 0 || !monitor1.getLastEvent().workersWithStartedActions().containsKey(metalworker0));
            assertTrue(monitor2.getEvents().size() == 0 || !monitor2.getLastEvent().workersWithStartedActions().containsKey(metalworker0));

            map.stepTime();
        }

        assertFalse(metalworker0.isHammering());
        assertFalse(metalworker0.isSawing());
        assertTrue(metalworker0.isWipingSweat());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().workersWithStartedActions().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(metalworker0), WorkerAction.WIPE_OFF_SWEAT_TO_MAKE_TOOL);
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().workersWithStartedActions().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(metalworker0), WorkerAction.WIPE_OFF_SWEAT_TO_MAKE_TOOL);
        assertTrue(monitor2.getEvents().isEmpty() ||
                        (!monitor2.getLastEvent().workersWithStartedActions().containsKey(metalworker0) &&
                            !monitor2.getLastEvent().workersWithStartedActions().containsKey(metalworker0)));

        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        for (int i = 0; i < 30; i++) {
            assertFalse(metalworker0.isHammering());
            assertFalse(metalworker0.isSawing());
            assertTrue(metalworker0.isWipingSweat());
            assertFalse(metalworker0.isInsideBuilding());
            assertTrue(metalworks.getFlag().getStackedCargo().isEmpty());
            assertNull(metalworker0.getCargo());
            assertTrue(monitor0.getEvents().size() == 0 || !monitor0.getLastEvent().workersWithStartedActions().containsKey(metalworker0));
            assertTrue(monitor1.getEvents().size() == 0 || !monitor1.getLastEvent().workersWithStartedActions().containsKey(metalworker0));
            assertTrue(monitor2.getEvents().size() == 0 || !monitor2.getLastEvent().workersWithStartedActions().containsKey(metalworker0));

            map.stepTime();
        }

        assertTrue(monitor0.getLastEvent().workersWithNewTargets().contains(metalworker0));
        assertTrue(monitor1.getLastEvent().workersWithNewTargets().contains(metalworker0));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithNewTargets().contains(metalworker0));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithNewTargets().contains(metalworker0));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithNewTargets().contains(metalworker0));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithNewTargets().contains(metalworker0));
    }

    @Test
    public void testEventIsSentWhenButcherWorks() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Place slaughterhouse
        var point3 = new Point(7, 9);
        var slaughterHouse = map.placeBuilding(new SlaughterHouse(player0), point3);

        // Connect the slaughterhouse with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, slaughterHouse.getFlag(), headquarter0.getFlag());

        // Finish construction of the slaughterhouse
        Utils.constructHouse(slaughterHouse);

        // Populate the slaughterhouse
        var butcher = Utils.occupyBuilding(new Butcher(player0, map), slaughterHouse);

        assertTrue(butcher.isInsideBuilding());
        assertEquals(butcher.getHome(), slaughterHouse);
        assertEquals(slaughterHouse.getWorker(), butcher);
        assertFalse(slaughterHouse.isWorking());

        // Deliver pig to the slaughterhouse
        Utils.deliverCargo(slaughterHouse, PIG);

        // Start monitoring
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the butcher starts to slaughter
        Utils.waitForButcherToWork(butcher, map);

        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().workersWithStartedActions().size() > 0);
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(butcher), WorkerAction.SLAUGHTERING);
        assertTrue(monitor0.getLastEvent().newWorkersOutside().contains(butcher));
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().workersWithStartedActions().size() > 0);
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(butcher), WorkerAction.SLAUGHTERING);
        assertTrue(monitor1.getLastEvent().newWorkersOutside().contains(butcher));
        assertTrue(monitor2.getEvents().isEmpty() ||
            (!monitor2.getLastEvent().workersWithStartedActions().containsKey(butcher) &&
                !monitor2.getLastEvent().newWorkersOutside().contains(butcher)));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(butcher));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(butcher));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(butcher));
    }

    @Test
    public void testOccupiedBakeryWithIngredientsProducesBread() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(9, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Place bakery
        var point3 = new Point(7, 9);
        var bakery = map.placeBuilding(new Bakery(player0), point3);

        // Connect the bakery with the headquarters
        var road0 = map.placeAutoSelectedRoad(player0, bakery.getFlag(), headquarter0.getFlag());

        // Finish construction of the bakery
        Utils.constructHouse(bakery);

        // Populate the bakery
        var baker = Utils.occupyBuilding(new Baker(player0, map), bakery);

        assertTrue(baker.isInsideBuilding());
        assertEquals(baker.getHome(), bakery);
        assertEquals(bakery.getWorker(), baker);
        assertFalse(bakery.isWorking());
        assertFalse(baker.isPuttingDoughIntoOven());
        assertFalse(baker.isTakingBreadOutOfOven());

        // Deliver material to the bakery
        Utils.deliverCargo(bakery, Material.WATER);
        Utils.deliverCargo(bakery, FLOUR);

        // Start monitoring
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that the baker rests
        for (int i = 0; i < 99; i++) {
            map.stepTime();

            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
            assertFalse(bakery.isWorking());
            assertFalse(baker.isPuttingDoughIntoOven());
            assertFalse(baker.isTakingBreadOutOfOven());
            assertTrue(baker.isInsideBuilding());
        }

        // Verify that the baker goes out to oven, carrying dough (halfway down-left) and the smoke starts coming from the bakery's chimney
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertEquals(baker.getTarget(), bakery.getPosition().downLeft());
        assertTrue(bakery.isWorking());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().changedBuildings().contains(bakery));
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().changedBuildings().contains(bakery));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor0.getLastEvent().changedBuildings().contains(bakery));

        for (int i = 0; i < 2_000; i++) {
            if (baker.getPercentageOfDistanceTraveled() == 50) {
                break;
            }

            monitor0.clearEvents();
            monitor1.clearEvents();
            monitor2.clearEvents();

            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
            assertTrue(bakery.isWorking());
            assertFalse(baker.isPuttingDoughIntoOven());
            assertFalse(baker.isTakingBreadOutOfOven());
            assertFalse(baker.isInsideBuilding());

            map.stepTime();
        }

        // Verify that the baker puts the bread into the oven
        assertTrue(bakery.isWorking());
        assertTrue(baker.isPuttingDoughIntoOven());
        assertFalse(baker.isTakingBreadOutOfOven());
        assertEquals(baker.getPercentageOfDistanceTraveled(), 50);
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().workersWithStartedActions().containsKey(baker));
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(baker), WorkerAction.OPEN_OVEN);
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().workersWithStartedActions().containsKey(baker));
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(baker), WorkerAction.OPEN_OVEN);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(baker));

        for (int i = 0; i < 29; i++) {
            map.stepTime();

            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
            assertFalse(baker.isInsideBuilding());
            assertTrue(bakery.isWorking());
            assertTrue(baker.isPuttingDoughIntoOven());
            assertFalse(baker.isTakingBreadOutOfOven());
            assertEquals(baker.getPercentageOfDistanceTraveled(), 50);
        }

        // Verify that the baker waits outside the oven
        for (int i = 0; i < 30; i++) {
            map.stepTime();

            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
            assertFalse(baker.isInsideBuilding());
            assertFalse(baker.isPuttingDoughIntoOven());
            assertFalse(baker.isTakingBreadOutOfOven());
            assertTrue(bakery.isWorking());
            assertEquals(baker.getPercentageOfDistanceTraveled(), 50);
        }

        // Verify that bread is done - baker takes out the bread and the smoke stops
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertFalse(bakery.isWorking());
        assertFalse(baker.isPuttingDoughIntoOven());
        assertTrue(baker.isTakingBreadOutOfOven());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().workersWithStartedActions().containsKey(baker));
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(baker), WorkerAction.OPEN_OVEN);
        assertTrue(monitor0.getLastEvent().changedBuildings().contains(bakery));
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().workersWithStartedActions().containsKey(baker));
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(baker), WorkerAction.OPEN_OVEN);
        assertTrue(monitor1.getLastEvent().changedBuildings().contains(bakery));
        assertTrue(monitor2.getEvents().isEmpty() ||
            (!monitor2.getLastEvent().workersWithStartedActions().containsKey(baker) &&
                !monitor2.getLastEvent().changedBuildings().contains(bakery)));

        for (int i = 0; i < 29; i++) {
            map.stepTime();

            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertNull(baker.getCargo());
            assertFalse(baker.isInsideBuilding());
            assertFalse(bakery.isWorking());
            assertFalse(baker.isPuttingDoughIntoOven());
            assertTrue(baker.isTakingBreadOutOfOven());
        }

        // Verify that the baker goes back to the bakery
        map.stepTime();

        assertEquals(baker.getTarget(), bakery.getPosition());
        assertEquals(baker.getNextPoint(), bakery.getPosition());
        assertEquals(baker.getCargo().getMaterial(), BREAD);
        assertFalse(bakery.isWorking());
        assertFalse(baker.isPuttingDoughIntoOven());
        assertFalse(baker.isTakingBreadOutOfOven());

        for (int i = 0; i < 29; i++) {
            if (baker.getPosition().equals(bakery.getPosition())) {
                break;
            }

            assertEquals(baker.getTarget(), bakery.getPosition());
            assertEquals(baker.getNextPoint(), bakery.getPosition());
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertEquals(baker.getCargo().getMaterial(), BREAD);
            assertFalse(bakery.isWorking());
            assertFalse(baker.isPuttingDoughIntoOven());
            assertFalse(baker.isTakingBreadOutOfOven());

            monitor0.clearEvents();
            monitor1.clearEvents();
            monitor2.clearEvents();

            map.stepTime();
        }

        assertTrue(baker.isInsideBuilding());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().removedWorkers().contains(baker));
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().removedWorkers().contains(baker));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().removedWorkers().contains(baker));

        // Verify that the baker waits a little bit inside the bakery ??
        for (int i = 0; i < 30; i++) {
            assertTrue(bakery.getFlag().getStackedCargo().isEmpty());
            assertTrue(baker.isInsideBuilding());
            assertFalse(bakery.isWorking());
            assertFalse(baker.isPuttingDoughIntoOven());
            assertFalse(baker.isTakingBreadOutOfOven());

            map.stepTime();
        }

        // Verify that the baker goes out to place the bread at the flag
        assertEquals(baker.getTarget(), bakery.getFlag().getPosition());

        for (int i = 0; i < 2_000; i++) {
            if (Objects.equals(baker.getPosition(), bakery.getFlag().getPosition())) {
                break;
            }

            assertEquals(baker.getTarget(), bakery.getFlag().getPosition());
            assertFalse(bakery.isWorking());
            assertFalse(baker.isPuttingDoughIntoOven());
            assertFalse(baker.isTakingBreadOutOfOven());

            map.stepTime();
        }

        assertEquals(baker.getPercentageOfDistanceTraveled(), 100);
        assertFalse(bakery.getFlag().getStackedCargo().isEmpty());
        assertNull(baker.getCargo());

        // Verify that the baker goes back to the bakery
        assertEquals(baker.getTarget(), bakery.getPosition());

        for (int i = 0; i < 2_000; i++) {
            if (Objects.equals(baker.getPosition(), bakery.getPosition())) {
                break;
            }

            map.stepTime();

            assertEquals(baker.getTarget(), bakery.getPosition());
        }

        map.stepTime();

        assertEquals(baker.getPercentageOfDistanceTraveled(), 100);
        assertEquals(baker.getPosition(), bakery.getPosition());
        assertNull(baker.getCargo());
        assertTrue(baker.isInsideBuilding());
    }

    @Test
    public void testMonitoringEventWhenWellWorkerDrawsWater() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(5, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Place well
        var point3 = new Point(8, 6);
        var well = map.placeBuilding(new Well(player0), point3);

        // Connect the well with the headquarters
        var point4 = new Point(6, 4);
        var point5 = new Point(8, 4);
        var point6 = new Point(9, 5);
        var road0 = map.placeRoad(player0, point4, point5, point6);

        // Wait for the well to get constructed and populated
        Utils.waitForBuildingToBeConstructed(well);
        var wellWorker = (WellWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(well);

        assertTrue(wellWorker.isInsideBuilding());

        // Let the worker rest
        Utils.fastForward(99, map);

        // Let the worker go out to pump
        assertTrue(wellWorker.isInsideBuilding());
        assertFalse(well.isWorking());
        assertFalse(wellWorker.isPumpingWater());

        map.stepTime();

        assertFalse(wellWorker.isInsideBuilding());
        assertTrue(well.isWorking());
        assertEquals(wellWorker.getTarget(), well.getPosition().downLeft());

        map.stepTime();

        // Start monitoring
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the well worker starts pumping water
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        for (int i = 0; i < 2_000; i++) {
            if (wellWorker.getPercentageOfDistanceTraveled() >= 50) {
                break;
            }

            assertEquals(wellWorker.getTarget(), well.getPosition().downLeft());
            assertFalse(wellWorker.isPumpingWater());

            map.stepTime();
        }

        assertTrue(wellWorker.getPercentageOfDistanceTraveled() >= 50);
        assertTrue(wellWorker.getPercentageOfDistanceTraveled() < 100);
        assertTrue(wellWorker.isPumpingWater());
        assertTrue(monitor0.getEvents().size() > 0);
        assertTrue(monitor0.getLastEvent().workersWithStartedActions().containsKey(wellWorker));
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(wellWorker), WorkerAction.DRAW_WATER_1);
        assertTrue(monitor1.getEvents().size() > 0);
        assertTrue(monitor1.getLastEvent().workersWithStartedActions().containsKey(wellWorker));
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(wellWorker), WorkerAction.DRAW_WATER_1);
        assertTrue(monitor2.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(wellWorker));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithStartedActions().containsKey(wellWorker));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithStartedActions().containsKey(wellWorker));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithStartedActions().containsKey(wellWorker));
    }


    @Test
    public void testMonitoringEventWhenSawmillWorkerWorks() throws Exception {

        // Create player list with two players
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        var player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0, player1, player2), 100, 100);

        // Place headquarters
        var point0 = new Point(5, 5);
        var point1 = new Point(37, 15);
        var point2 = new Point(90, 90);

        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);
        var headquarter1 = map.placeBuilding(new Headquarter(player1), point1);
        var headquarter2 = map.placeBuilding(new Headquarter(player2), point2);

        // Let player 1 discover the full world
        GameUtils.discoverFullMap(player1);

        // Place sawmill
        var point3 = new Point(7, 9);
        var sawmill = map.placeBuilding(new Sawmill(player0), point3);

        // Finish construction of the sawmill
        Utils.constructHouse(sawmill);

        // Occupy the sawmill
        var sawmillWorker0 = Utils.occupyBuilding(new Carpenter(player0, map), sawmill);

        assertTrue(sawmillWorker0.isInsideBuilding());
        assertEquals(sawmillWorker0.getHome(), sawmill);
        assertEquals(sawmill.getWorker(), sawmillWorker0);

        // Deliver wood to the sawmill
        Utils.deliverCargo(sawmill, WOOD);
        Utils.deliverCargo(sawmill, WOOD);

        // Start monitoring
        var monitor0 = new Utils.GameViewMonitor();
        var monitor1 = new Utils.GameViewMonitor();
        var monitor2 = new Utils.GameViewMonitor();

        player0.monitorGameView(monitor0);
        player1.monitorGameView(monitor1);
        player2.monitorGameView(monitor2);

        // Verify that an event is sent when the sawmill worker starts sawing
        assertFalse(sawmill.isWorking());

        for (int i = 0; i < 2_000; i++) {
            if (sawmillWorker0.isWorking()) {
                break;
            }

            assertFalse(sawmill.isWorking());
            assertFalse(sawmillWorker0.isWorking());

            map.stepTime();
        }

        assertTrue(sawmillWorker0.isWorking());
        assertEquals(monitor0.getLastEvent().workersWithStartedActions().get(sawmillWorker0), SAWING);
        assertTrue(monitor0.getLastEvent().newWorkersOutside().contains(sawmillWorker0));
        assertTrue(monitor0.getLastEvent().changedBuildings().contains(sawmill));
        assertEquals(monitor1.getLastEvent().workersWithStartedActions().get(sawmillWorker0), SAWING);
        assertTrue(monitor1.getLastEvent().newWorkersOutside().contains(sawmillWorker0));
        assertTrue(monitor1.getLastEvent().changedBuildings().contains(sawmill));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().workersWithStartedActions().containsKey(sawmillWorker0) &&
                        !monitor2.getLastEvent().newWorkersOutside().contains(sawmillWorker0) &&
                        !monitor2.getLastEvent().changedBuildings().contains(sawmill)));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() ||
                (!monitor0.getLastEvent().workersWithStartedActions().containsKey(sawmillWorker0) &&
                        !monitor0.getLastEvent().newWorkersOutside().contains(sawmillWorker0) &&
                        !monitor0.getLastEvent().changedBuildings().contains(sawmill)));
        assertTrue(monitor1.getEvents().isEmpty() ||
                (!monitor1.getLastEvent().workersWithStartedActions().containsKey(sawmillWorker0) &&
                        !monitor1.getLastEvent().newWorkersOutside().contains(sawmillWorker0) &&
                        !monitor1.getLastEvent().changedBuildings().contains(sawmill)));
        assertTrue(monitor2.getEvents().isEmpty() ||
                (!monitor2.getLastEvent().workersWithStartedActions().containsKey(sawmillWorker0) &&
                        !monitor2.getLastEvent().newWorkersOutside().contains(sawmillWorker0) &&
                        !monitor2.getLastEvent().changedBuildings().contains(sawmill)));

        // Verify that an event is sent when the carpenter is done sawing
        for (int i = 0; i < 2_000; i++) {
            if (!sawmillWorker0.isWorking()) {
                break;
            }

            map.stepTime();
        }

        assertFalse(sawmillWorker0.isWorking());
        assertFalse(sawmillWorker0.isInsideBuilding());
        assertTrue(monitor0.getLastEvent().workersWithNewTargets().contains(sawmillWorker0));
        assertTrue(monitor1.getLastEvent().workersWithNewTargets().contains(sawmillWorker0));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithNewTargets().contains(sawmillWorker0));

        // Verify that the event is only sent once
        monitor0.clearEvents();
        monitor1.clearEvents();
        monitor2.clearEvents();

        map.stepTime();

        assertTrue(monitor0.getEvents().isEmpty() || !monitor0.getLastEvent().workersWithNewTargets().contains(sawmillWorker0));
        assertTrue(monitor1.getEvents().isEmpty() || !monitor1.getLastEvent().workersWithNewTargets().contains(sawmillWorker0));
        assertTrue(monitor2.getEvents().isEmpty() || !monitor2.getLastEvent().workersWithNewTargets().contains(sawmillWorker0));
    }
}
