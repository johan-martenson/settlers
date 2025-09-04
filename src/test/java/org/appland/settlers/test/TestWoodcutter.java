package org.appland.settlers.test;

import org.appland.settlers.assets.Nation;
import org.appland.settlers.model.Cargo;
import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Flag;
import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.Player;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.PlayerType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Road;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.actors.Courier;
import org.appland.settlers.model.actors.WoodcutterWorker;
import org.appland.settlers.model.actors.Worker;
import org.appland.settlers.model.buildings.Building;
import org.appland.settlers.model.buildings.ForesterHut;
import org.appland.settlers.model.buildings.Fortress;
import org.appland.settlers.model.buildings.Headquarter;
import org.appland.settlers.model.buildings.Sawmill;
import org.appland.settlers.model.buildings.Storehouse;
import org.appland.settlers.model.buildings.Woodcutter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.actors.Soldier.Rank.PRIVATE_RANK;
import static org.appland.settlers.test.Utils.constructHouse;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TestWoodcutter {

    @Test
    public void testWoodcutterOnlyNeedsTwoPlanksForConstruction() throws Exception {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place woodcutter
        Point point22 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point22);

        // Deliver two planks
        Cargo cargo = new Cargo(PLANK, map);

        woodcutter0.putCargo(cargo);
        woodcutter0.putCargo(cargo);

        // Assign builder
        Utils.assignBuilder(woodcutter0);

        // Verify that this is enough to construct the woodcutter
        for (int i = 0; i < 100; i++) {
            assertTrue(woodcutter0.isUnderConstruction());

            map.stepTime();
        }

        assertTrue(woodcutter0.isReady());
    }

    @Test
    public void testWoodcutterCannotBeConstructedWithOnePlank() throws Exception {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point21 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point21);

        // Place woodcutter
        Point point22 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point22);

        // Deliver two planks
        Cargo cargo = new Cargo(PLANK, map);

        woodcutter0.putCargo(cargo);

        // Assign builder
        Utils.assignBuilder(woodcutter0);

        // Verify that this is enough to construct the woodcutter
        for (int i = 0; i < 500; i++) {
            assertTrue(woodcutter0.isUnderConstruction());

            map.stepTime();
        }

        assertFalse(woodcutter0.isReady());
    }

    @Test
    public void testUnfinishedWoodcutterNeedsNoWoodcutter() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(8, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Verify the the woodcutter doesn't need any worker when it's under construction
        assertTrue(woodcutter.isPlanned());
        assertFalse(woodcutter.needsWorker());
    }

    @Test
    public void testFinishedWoodcutterNeedsWoodcutterWorker() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(8, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter hut to the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Wait for the woodcutter to get constructed
        Utils.waitForBuildingToBeConstructed(woodcutter);

        // Verify that it needs a worker
        assertTrue(woodcutter.needsWorker());
    }

    @Test
    public void testWoodcutterIsAssignedToFinishedHouse() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(8, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Finish the woodcutter
        constructHouse(woodcutter);

        // Run game logic twice, once to place courier and once to place woodcutter worker
        Utils.fastForward(2, map);

        boolean foundWoodcutter = false;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                foundWoodcutter = true;

                break;
            }
        }

        assertTrue(foundWoodcutter);
    }

    @Test
    public void testWoodcutterIsNotASoldier() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(8, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Finish the woodcutter
        constructHouse(woodcutter);

        // Wait for a woodcutter to walk out
        WoodcutterWorker woodcutterWorker0 = Utils.waitForWorkerOutsideBuilding(WoodcutterWorker.class, player0);

        // Verify that the woodcutter is not a soldier
        assertFalse(woodcutterWorker0.isSoldier());
    }

    @Test
    public void testWoodcutterIsCreatedFromTools() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(8, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Remove all woodcutter workers and add an axe to the headquarters
        Utils.adjustInventoryTo(headquarter0, WOODCUTTER_WORKER, 0);
        Utils.adjustInventoryTo(headquarter0, Material.AXE, 1);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter0.getFlag());

        // Finish the woodcutter
        constructHouse(woodcutter);

        // Run game logic twice, once to place courier and once to place woodcutter worker
        Utils.fastForward(2, map);

        boolean foundWoodcutter = false;
        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                foundWoodcutter = true;

                break;
            }
        }

        assertTrue(foundWoodcutter);
    }

    @Test
    public void testArrivedWoodcutterRestsInHutAndThenLeaves() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place and grow tree
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place the woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter);

        assertTrue(wcWorker.isInsideBuilding());

        // Run the game logic 99 times and make sure the forester stays in the hut
        for (int i = 0; i < 9; i++) {
            assertTrue(wcWorker.isInsideBuilding());
            Utils.fastForward(10, map);
        }

        Utils.fastForward(9, map);

        assertTrue(wcWorker.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());
    }

    @Test
    public void testWoodcutterFindsSpotToCutDownTree() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place and grow tree
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place the woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter);

        assertTrue(wcWorker.isInsideBuilding());

        // Wait for woodcutter worker to leave the hut
        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();
        assertNotNull(point);

        assertEquals(point, point2);
        assertTrue(map.isTreeAtPoint(point));
        assertTrue(wcWorker.isTraveling());
        assertFalse(map.isBuildingAtPoint(point));
        assertFalse(map.isRoadAtPoint(point));
        assertFalse(map.isFlagAtPoint(point));
    }

    @Test
    public void testWoodcutterReachesPointToCutDownTree() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place and grow tree
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place the woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        Worker wcWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter);

        // Wait for woodcutter worker to rest
        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(wcWorker.getTarget(), point2);
        assertTrue(wcWorker.isTraveling());

        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertEquals(wcWorker.getPosition(), point);
        assertFalse(wcWorker.isTraveling());
        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));
    }

    @Test
    public void testWoodcutterDoesntWalkThroughStoneToReachTree() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 100, 100);

        // Place headquarters
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place and grow tree
        Point point2 = new Point(17, 3);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place stones
        var point3 = new Point(15, 3);
        var point4 = new Point(14, 4);
        var point5 = new Point(14, 2);
        var stone0 = map.placeStone(point3, Stone.StoneType.STONE_1, 6);
        var stone1 = map.placeStone(point4, Stone.StoneType.STONE_1, 6);
        var stone2 = map.placeStone(point5, Stone.StoneType.STONE_1, 6);

        // Place the woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        Worker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter);

        // Wait for the woodcutter to leave the hut
        Utils.waitForWorkerToBeOutside(woodcutterWorker, map);

        // Verify that the woodcutter worker goes to the tree without passing through the stones
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), tree.getPosition());

        for (int i = 0; i < 200; i++) {
            assertNotEquals(woodcutterWorker.getPosition(), point3);
            assertNotEquals(woodcutterWorker.getPosition(), point4);
            assertNotEquals(woodcutterWorker.getPosition(), point5);

            if (woodcutterWorker.isExactlyAtPoint() && woodcutterWorker.getPosition().equals(tree.getPosition())) {
                break;
            }

            map.stepTime();
        }

        assertTrue(woodcutterWorker.isExactlyAtPoint());
        assertEquals(woodcutterWorker.getPosition(), tree.getPosition());
    }

    @Test
    public void testWoodcutterCutsDownTree() throws Exception {

        // Create single player game
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 20, 20);

        // Place headquarters
        var point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place and grow the tree
        var point2 = new Point(12, 4);
        var tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place the woodcutter
        var point1 = new Point(10, 4);
        var woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        var wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);

        // Wait for the woodcutter to rest
        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());
        assertTrue(map.isTreeAtPoint(point2));

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());

        var point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());

        // Let the woodcutter reach the tree and start cutting
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();

        assertTrue(wcWorker.isCuttingTree());
        assertNull(wcWorker.getCargo());

        // Wait for the woodcutter to finish cutting the tree
        for (int i = 0; i < 49; i++) {
            assertTrue(wcWorker.isCuttingTree());
            assertTrue(map.isTreeAtPoint(point));

            map.stepTime();
        }

        // Wait for the tree to fall
        for (int i = 0; i < 10; i++) {
            assertTrue(map.isTreeAtPoint(point));
            assertEquals(tree, map.getTreeAtPoint(point));
            assertTrue(tree.isFalling());
            assertTrue(map.getTrees().contains(tree));
            assertFalse(wcWorker.isCuttingTree());
            assertFalse(wcWorker.isTraveling());
            assertEquals(wcWorker.getPosition(), map.getTreeAtPoint(point).getPosition());

            map.stepTime();
        }

        assertTrue(map.isDecoratedAtPoint(point));
        assertEquals(map.getDecorationAtPoint(point), DecorationType.TREE_STUB);
        assertFalse(map.isTreeAtPoint(point));
        assertNull(map.getTreeAtPoint(point));
        assertFalse(map.getTrees().contains(tree));
        assertFalse(wcWorker.isCuttingTree());

        map.stepTime();

        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());
        assertNotNull(wcWorker.getCargo());
        assertEquals(wcWorker.getCargo().getMaterial(), WOOD);

        // Verify that the woodcutter has got a wood cargo and is walking back to the hut
        assertFalse(wcWorker.isCuttingTree());
        assertFalse(map.isTreeAtPoint(point));
        assertNotNull(wcWorker.getCargo());
        assertEquals(wcWorker.getCargo().getMaterial(), WOOD);
    }

    @Test
    public void testWoodcutterReturnsAndStoresWoodAsCargo() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Place and grow the tree
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Wait for the tree to grow
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place the woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, woodcutter);


        // Wait for the woodcutter to rest
        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());

        Point point = wcWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(wcWorker.isTraveling());

        // Let the woodcutter reach the tree
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point));

        map.stepTime();

        assertTrue(wcWorker.isCuttingTree());

        // Wait for the woodcutter to cut down the tree
        Utils.waitForTreeToDisappearFromMap(tree, map);

        map.stepTime();

        // The woodcutter has cut down the tree and goes back via the flag
        assertFalse(wcWorker.isCuttingTree());
        assertNotNull(wcWorker.getCargo());

        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, woodcutter.getPosition());

        assertTrue(wcWorker.isInsideBuilding());
        assertNotNull(wcWorker.getCargo());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        // Woodcutter leaves the building and puts the cargo on the building's flag
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), woodcutter.getFlag().getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        // Let the woodcutter reach the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, woodcutter.getFlag().getPosition());

        assertFalse(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());

        Cargo cargo = woodcutter.getFlag().getStackedCargo().getFirst();

        assertEquals(cargo.getTarget(), headquarter);

        // Let the woodcutter go back to the hut
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        map.stepTime();

        // Verify that the woodcutter remains in the hut
        assertTrue(wcWorker.isInsideBuilding());

        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());
    }

    @Test
    public void testWoodCargoIsDeliveredToSawmillWhichIsCloserThanHeadquarters() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Place and grow the tree
        Point point2 = new Point(14, 6);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Wait for the tree to grow
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Remove all wood from the headquarters
        Utils.adjustInventoryTo(headquarter, WOOD, 0);

        // Place sawmill
        Point point4 = new Point(10, 4);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point4);

        // Connect the sawmill to the headquarters
        Road road2 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Wait for the sawmill to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(sawmill);

        Utils.waitForNonMilitaryBuildingToGetPopulated(sawmill);

        // Place the woodcutter
        Point point1 = new Point(14, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the sawmill
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), sawmill.getFlag());

        // Wait for the woodcutter to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(woodcutter);

        WoodcutterWorker wcWorker = (WoodcutterWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        // Wait for the courier on the road between the sawmill and the woodcutter hut to have a wood cargo
        Utils.waitForFlagToGetStackedCargo(map, woodcutter.getFlag(), 1);

        assertEquals(woodcutter.getFlag().getStackedCargo().getFirst().getMaterial(), WOOD);

        // Wait for the courier to pick up the wood cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that the courier delivers the cargo to the sawmill (and not the headquarters)
        assertEquals(sawmill.getAmount(WOOD), 0);

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), sawmill.getPosition());

        assertEquals(sawmill.getAmount(WOOD), 1);
    }

    @Test
    public void testWoodIsNotDeliveredToStorehouseUnderConstruction() throws InvalidUserActionException {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Adjust the inventory so that there are no planks and no wood
        Utils.adjustInventoryTo(headquarter, PLANK, 0);
        Utils.adjustInventoryTo(headquarter, WOOD, 0);

        // Place tree
        Point point2 = new Point(14, 6);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Place store house
        Point point4 = new Point(10, 4);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point4);

        // Connect the store house to the headquarters
        Road road2 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter.getFlag());

        // Place the woodcutter
        Point point1 = new Point(14, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the store house
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), storehouse.getFlag());

        // Deliver the needed planks to the woodcutter
        Utils.deliverCargos(woodcutter, PLANK, 2);

        // Wait for the woodcutter to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(woodcutter);

        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        // Wait for the courier on the road between the store house and the woodcutter hut to have a wood cargo
        Utils.waitForFlagToGetStackedCargo(map, woodcutter.getFlag(), 1);

        assertEquals(woodcutter.getFlag().getStackedCargo().getFirst().getMaterial(), WOOD);

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that the courier delivers the cargo to the store house's flag so that it can continue to the headquarters
        assertEquals(headquarter.getAmount(WOOD), 0);
        assertEquals(woodcutter.getAmount(WOOD), 0);
        assertFalse(storehouse.needsMaterial(WOOD));
        assertTrue(storehouse.isUnderConstruction());

        Utils.fastForwardUntilWorkerReachesPoint(map, road0.getCourier(), storehouse.getFlag().getPosition());

        assertEquals(storehouse.getFlag().getStackedCargo().size(), 1);
        assertTrue(storehouse.getFlag().getStackedCargo().getFirst().getMaterial().equals(WOOD));
        assertNull(road0.getCourier().getCargo());
    }

    @Test
    public void testWoodIsNotDeliveredTwiceToBuildingThatOnlyNeedsOne() throws InvalidUserActionException {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Adjust the inventory so that there is no wood
        Utils.adjustInventoryTo(headquarter, WOOD, 0);

        // Place tree
        Point point2 = new Point(14, 6);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Place sawmill
        Point point4 = new Point(10, 4);
        Sawmill sawmill = map.placeBuilding(new Sawmill(player0), point4);

        // Construct the sawmill
        Utils.constructHouse(sawmill);

        // Connect the sawmill to the headquarters
        Road road2 = map.placeAutoSelectedRoad(player0, sawmill.getFlag(), headquarter.getFlag());

        // Place the woodcutter
        Point point1 = new Point(14, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the sawmill
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), sawmill.getFlag());

        // Deliver the needed planks to the woodcutter
        Utils.deliverCargos(woodcutter, PLANK, 2);

        // Wait for the woodcutter to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(woodcutter);

        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        // Wait for the flag on the road between the sawmill and the woodcutter to have a tree cargo
        Utils.waitForFlagToGetStackedCargo(map, woodcutter.getFlag(), 1);

        assertEquals(woodcutter.getFlag().getStackedCargo().getFirst().getMaterial(), WOOD);

        // Deliver wood to the sawmill so it only needs one more tree
        Utils.deliverCargos(sawmill, WOOD, 5);

        assertEquals(sawmill.getAmount(WOOD), 5);
        assertFalse(sawmill.needsMaterial(WOOD));
        assertEquals(sawmill.getCanHoldAmount(WOOD), 6);

        // Stop production in the sawmill so it doesn't consume it's wood
        sawmill.stopProduction();

        // Wait for the courier to pick up the cargo
        Utils.fastForwardUntilWorkerCarriesCargo(map, road0.getCourier());

        // Verify that no tree is delivered from the headquarters
        Utils.adjustInventoryTo(headquarter, WOOD, 1);

        assertEquals(sawmill.getCanHoldAmount(WOOD) - sawmill.getAmount(WOOD), 1);
        assertFalse(sawmill.needsMaterial(WOOD));

        for (int i = 0; i < 200; i++) {
            assertNull(headquarter.getWorker().getCargo());

            map.stepTime();
        }

        assertEquals(headquarter.getAmount(WOOD), 1);
    }

    @Test
    public void testWoodCargoIsCorrect() throws Exception {

        // Create players
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Place and grow the tree
        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place forester hut (is this needed for the test?)
        Point point0 = new Point(14, 4);
        Building hut = map.placeBuilding(new ForesterHut(player0), point0);

        // Place woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Place forester
        Point point4 = new Point(12, 12);
        Building hut2 = map.placeBuilding(new ForesterHut(player0), point4);

        // Construct the forester hut
        constructHouse(woodcutter);
        constructHouse(hut);
        constructHouse(hut2);

        // Place roads
        Point point5 = new Point(9, 3);

        Road road0 = map.placeRoad(player0, headquarter.getFlag().getPosition(), point5, woodcutter.getFlag().getPosition());
        Road road1 = map.placeAutoSelectedRoad(player0, hut.getFlag(), woodcutter.getFlag());
        Road road2 = map.placeAutoSelectedRoad(player0, hut2.getFlag(), headquarter.getFlag());

        // Verify that the woodcutter is occupied
        WoodcutterWorker woodcutterWorker = (WoodcutterWorker) Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        // Wait for the woodcutter to rest
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        Utils.fastForward(99, map);

        assertTrue(woodcutterWorker.isInsideBuilding());

        // Step once and make sure the forester goes out of the hut
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());

        Point point = woodcutterWorker.getTarget();

        assertEquals(point, point2);
        assertTrue(woodcutterWorker.isTraveling());

        // Let the woodcutter reach the tree
        Utils.fastForwardUntilWorkersReachTarget(map, woodcutterWorker);

        assertTrue(woodcutterWorker.isArrived());
        assertTrue(woodcutterWorker.isAt(point));

        map.stepTime();

        assertTrue(woodcutterWorker.isCuttingTree());

        // Wait for the woodcutter to cut down the tree
        Utils.waitForWoodcutterToStopCutting(woodcutterWorker, map);

        // Wait for the tree to fall down
        assertFalse(woodcutterWorker.isCuttingTree());
        assertTrue(map.getTreeAtPoint(point).isFalling());

        Utils.waitForTreeToDisappearFromMap(tree, map);

        map.stepTime();

        // The woodcutter has cut down the tree and goes back via the flag
        assertEquals(woodcutterWorker.getTarget(), woodcutter.getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertTrue(woodcutterWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter.getPosition());

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertNotNull(woodcutterWorker.getCargo());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        // Woodcutter leaves the building and puts the cargo on the building's flag
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), woodcutter.getFlag().getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        // Let the woodcutter reach the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter.getFlag().getPosition());

        // Verify that the cargo is setup correctly
        assertFalse(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertNull(woodcutterWorker.getCargo());
        assertEquals(woodcutterWorker.getTarget(), woodcutter.getPosition());

        Cargo cargo = woodcutter.getFlag().getStackedCargo().getFirst();

        assertEquals(cargo.getTarget(), headquarter);
    }

    @Test
    public void testWoodcutterHutWithoutTreesProducesNothing() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, woodcutter);

        assertTrue(wcWorker.isInsideBuilding());
        assertNull(wcWorker.getCargo());

        for (int i = 0; i < 100; i++) {
            map.stepTime();
            assertNull(wcWorker.getCargo());
        }
    }

    @Test
    public void testWoodcutterOnlyCutsDownFullGrownTrees() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the woodcutter hut
        constructHouse(woodcutter);

        // Place the woodcutter
        WoodcutterWorker woodcutterWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(woodcutterWorker, woodcutter);

        // Run the game logic 99 times and make sure the forester stays in the hut
        for (int i = 0; i < 9; i++) {
            Utils.fastForward(10, map);
        }

        Utils.fastForward(9, map);

        assertTrue(woodcutterWorker.isInsideBuilding());

        Point point2 = new Point(12, 4);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.NEWLY_PLANTED);

        assertEquals(tree.getSize(), Tree.TreeSize.NEWLY_PLANTED);

        // Step once and make sure the forester stays in the hut
        map.stepTime();

        assertTrue(woodcutterWorker.isInsideBuilding());

        // Grow tree to small
        for (int i = 0; i < 500; i++) {
            map.stepTime();

            if (tree.getSize() == Tree.TreeSize.SMALL) {
                break;
            }
        }

        // Grow tree to medium
        for (int i = 0; i < 500; i++) {
            map.stepTime();

            if (tree.getSize() == Tree.TreeSize.MEDIUM) {
                break;
            }
        }

        assertEquals(tree.getSize(), Tree.TreeSize.MEDIUM);

        // Step once and make sure the forester stays in the hut
        map.stepTime();

        assertTrue(woodcutterWorker.isInsideBuilding());

        // Grow the tree to large
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Step time and make sure the forester leaves the hut
        map.stepTime();

        assertFalse(woodcutterWorker.isInsideBuilding());
    }

    @Test
    public void testWoodcutterDoesNotCutDownPineappleTrees() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter hut to the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Wait for the woodcutter to get constructed
        Utils.waitForBuildingToBeConstructed(woodcutter);

        // Wait for the woodcutter to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(woodcutter);

        WoodcutterWorker woodcutterWorker = (WoodcutterWorker) woodcutter.getWorker();

        assertNotNull(woodcutterWorker);

        // Surround the woodcutter completely with pineapple trees that can't be cut down
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {

                if ((x + y) % 2 != 0) {
                    continue;
                }

                Point point = new Point(x, y);

                if (map.isBuildingAtPoint(point) || map.isFlagAtPoint(point) || map.isRoadAtPoint(point)) {
                    continue;
                }

                Tree tree = map.placeTree(point, Tree.TreeType.PINE_APPLE, Tree.TreeSize.FULL_GROWN);

                tree.setSize(Tree.TreeSize.FULL_GROWN);
            }
        }

        // All pine apple trees should be fully grown
        for (Tree tree : map.getTrees()) {
            assertEquals(tree.getSize(), Tree.TreeSize.FULL_GROWN);
        }

        // Verify that the woodcutter doesn't go out to cut down any pine apple tree
        for (int i = 0; i < 1000; i++) {
            assertTrue(woodcutterWorker.isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testWoodcutterGoesOutToCutTreesSeveralTimes() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point3 = new Point(6, 4);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point3);

        // Plant and grow trees
        Point point2 = new Point(12, 4);
        Tree tree0 = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Place tree
        Point point5 = new Point(11, 5);
        Tree tree1 = map.placeTree(point5, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Wait for the tree to grow
        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        // Place the woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Construct the forester hut
        constructHouse(woodcutter);

        // Manually place forester
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(wcWorker, woodcutter);

        // Wait for the woodcutter worker to leave the hut
        Utils.fastForward(100, map);

        assertFalse(wcWorker.isInsideBuilding());

        assertTrue(wcWorker.isTraveling());
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));

        // Let the woodcutter reach the tree
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());

        map.stepTime();

        assertTrue(wcWorker.isCuttingTree());

        // Wait for the woodcutter to cut down the tree
        Utils.waitForTreeToDisappearFromMap(tree0, map);

        map.stepTime();

        // The woodcutter has cut down the tree and goes back to the hut
        assertFalse(wcWorker.isCuttingTree());
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, woodcutter.getPosition());

        // Woodcutter enters building but does not store the cargo yet
        assertTrue(wcWorker.isInsideBuilding());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertEquals(woodcutter.getPosition(), wcWorker.getPosition());
        assertNotNull(wcWorker.getCargo());

        // Woodcutter leaves the building and puts the cargo on the building's flag
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());
        assertEquals(wcWorker.getTarget(), woodcutter.getFlag().getPosition());
        assertTrue(woodcutter.getFlag().getStackedCargo().isEmpty());

        // Let the woodcutter reach the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker, woodcutter.getFlag().getPosition());

        assertFalse(woodcutter.getFlag().getStackedCargo().isEmpty());
        assertNull(wcWorker.getCargo());
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());

        Cargo cargo = woodcutter.getFlag().getStackedCargo().getFirst();

        assertEquals(cargo.getTarget(), headquarter);

        // Let the woodcutter go back to the hut
        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isInsideBuilding());

        // Let the woodcutter rest
        Utils.fastForward(99, map);

        assertTrue(wcWorker.isInsideBuilding());

        // Verify that the woodcutter goes out again
        map.stepTime();

        assertFalse(wcWorker.isInsideBuilding());

    }

    @Test
    public void testPositionIsCorrectWhenWoodcutterEntersHut() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(8, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Finish the woodcutter
        constructHouse(woodcutter);

        // Run game logic twice, once to place courier and once to place woodcutter worker
        Utils.fastForward(2, map);

        WoodcutterWorker wcWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                wcWorker = (WoodcutterWorker)worker;
            }
        }

        assertNotNull(wcWorker);
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertEquals(wcWorker.getPosition(), woodcutter.getPosition());
    }

    @Test
    public void testWoodcutterWithoutConnectedStorageKeepsProducing() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Plant and grow trees
        Point point2 = new Point(10, 8);
        Tree tree0 = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Point point3 = new Point(11, 7);
        Tree tree1 = map.placeTree(point3, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        // Place woodcutter
        Point point26 = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Let the woodcutter worker rest
        Utils.fastForward(100, map);

        // Wait for the woodcutter worker to go to the tree
        var worker = (WoodcutterWorker) woodcutter0.getWorker();

        assertTrue(worker.getTarget().equals(tree0.getPosition()) || worker.getTarget().equals(tree1.getPosition()));
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        // Wait for the woodcutter to cut the tree
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        map.stepTime();

        assertTrue(worker.isCuttingTree());
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());
        assertTrue(map.isTreeAtPoint(tree0.getPosition()));
        assertTrue(map.isTreeAtPoint(tree1.getPosition()));
        assertFalse(tree0.isFalling());
        assertFalse(tree1.isFalling());

        Utils.waitForTreeToDisappearFromMap(map.getTreeAtPoint(worker.getPosition()), map);

        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        map.stepTime();

        assertNotNull(worker.getCargo());
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        // Verify that the woodcutter worker puts the wood cargo at the flag
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter0.getFlag().getPosition());
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(woodcutter0.getFlag().getStackedCargo().isEmpty());

        // Wait for the worker to go back to the woodcutter
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        // Let the woodcutter worker rest
        Utils.fastForward(100, map);

        // Wait for the woodcutter worker to go to the next tree
        assertTrue(worker.getTarget().equals(tree0.getPosition()) || worker.getTarget().equals(tree1.getPosition()));

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        // Wait for the woodcutter to cut the tree
        Utils.waitForTreeToDisappearFromMap(map.getTreeAtPoint(worker.getPosition()), map);

        map.stepTime();

        assertNotNull(worker.getCargo());

        // Wait for the woodcutter worker to go back to the woodcutter
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        // Verify that the second cargo is put at the flag
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertEquals(woodcutter0.getFlag().getStackedCargo().size(), 2);
    }

    @Test
    public void testCargoProducedWithoutConnectedStorageAreDeliveredWhenStorageIsAvailable() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Plant and grow trees
        Point point2 = new Point(10, 8);
        Tree tree0 = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        // Place woodcutter
        Point point26 = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Let the woodcutter worker rest
        Utils.fastForward(100, map);

        // Wait for the woodcutter worker to go to the tree
        Worker worker = woodcutter0.getWorker();

        assertEquals(worker.getTarget(), tree0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, tree0.getPosition());

        // Wait for the woodcutter to cut the tree
        Utils.waitForTreeToDisappearFromMap(tree0, map);

        map.stepTime();

        assertNotNull(worker.getCargo());

        // Wait for the woodcutter worker to go back to the woodcutter
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        // Verify that the woodcutter worker puts the wood cargo at the flag
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter0.getFlag().getPosition());
        assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        assertNull(worker.getCargo());
        assertFalse(woodcutter0.getFlag().getStackedCargo().isEmpty());

        // Wait to let the cargo remain at the flag without any connection to the storage
        Cargo cargo = woodcutter0.getFlag().getStackedCargo().getFirst();

        Utils.fastForward(50, map);

        assertEquals(cargo.getPosition(), woodcutter0.getFlag().getPosition());

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        // Assign a courier to the road
        Courier courier = new Courier(player0, map);
        map.placeWorker(courier, headquarter0.getFlag());
        courier.assignToRoad(road0);

        // Wait for the courier to reach the idle point of the road
        assertNotEquals(courier.getTarget(), headquarter0.getFlag().getPosition());
        assertNotEquals(courier.getTarget(), woodcutter0.getFlag().getPosition());
        assertTrue(road0.getWayPoints().contains(courier.getTarget()));


        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        // Verify that the courier walks to pick up the cargo
        map.stepTime();

        assertEquals(courier.getTarget(), woodcutter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, courier.getTarget());

        // Verify that the courier has picked up the cargo
        assertNotNull(courier.getCargo());
        assertEquals(courier.getCargo(), cargo);

        // Verify that the courier delivers the cargo to the headquarters
        assertEquals(courier.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOOD);

        Utils.fastForwardUntilWorkerReachesPoint(map, courier, headquarter0.getPosition());

        // Verify that the courier has delivered the cargo to the headquarters
        assertNull(courier.getCargo());
        assertEquals(headquarter0.getAmount(WOOD), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerGoesBackToStorageWhenWoodcutterIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Destroy the woodcutter
        Worker worker = woodcutter0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

        // Verify that the woodcutter worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerGoesBackOnToStorageOnRoadsIfPossibleWhenWoodcutterIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Connect the woodcutter with the headquarters
        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Destroy the woodcutter
        Worker worker = woodcutter0.getWorker();

        assertTrue(worker.isInsideBuilding());
        assertEquals(worker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(worker.isInsideBuilding());
        assertEquals(worker.getTarget(), headquarter0.getPosition());

        // Verify that the worker plans to use the roads
        boolean firstStep = true;
        for (Point point : worker.getPlannedPath()) {
            if (firstStep) {
                firstStep = false;
                continue;
            }

            assertTrue(map.isRoadAtPoint(point));
        }
    }

    @Test
    public void testDestroyedWoodcutterIsRemovedAfterSomeTime() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Connect the woodcutter with the headquarters
        map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Destroy the woodcutter
        woodcutter0.tearDown();

        assertTrue(woodcutter0.isBurningDown());

        // Wait for the woodcutter to stop burning
        Utils.fastForward(50, map);

        assertTrue(woodcutter0.isDestroyed());

        // Wait for the woodcutter to disappear
        for (int i = 0; i < 100; i++) {
            assertEquals(map.getBuildingAtPoint(point26), woodcutter0);

            map.stepTime();
        }

        assertFalse(map.isBuildingAtPoint(point26));
        assertFalse(map.getBuildings().contains(woodcutter0));
        assertNull(map.getBuildingAtPoint(point26));
    }

    @Test
    public void testDrivewayIsRemovedWhenFlagIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Remove the flag and verify that the driveway is removed
        assertNotNull(map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition()));

        map.removeFlag(woodcutter0.getFlag());

        assertNull(map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition()));
    }

    @Test
    public void testDrivewayIsRemovedWhenBuildingIsRemoved() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(8, 8);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Tear down the building and verify that the driveway is removed
        assertNotNull(map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition()));

        woodcutter0.tearDown();

        assertNull(map.getRoad(woodcutter0.getPosition(), woodcutter0.getFlag().getPosition()));
    }

    @Test
    public void testProductionInWoodcutterCanBeStopped() throws Exception {

        // Create gamemap
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Plant and grow trees
        Point point12 = new Point(10, 8);
        Tree tree0 = map.placeTree(point12, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        // Place woodcutter
        Point point1 = new Point(8, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        // Finish the woodcutter
        constructHouse(woodcutter);

        // Assign a worker to the woodcutter
        WoodcutterWorker worker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(worker, woodcutter);

        assertTrue(worker.isInsideBuilding());

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the worker to produce wood
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), WOOD);

        // Wait for the worker to return to the woodcutter hut
        assertEquals(worker.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter.getPosition());

        // Wait for the worker to deliver the cargo
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter.getFlag().getPosition());

        // Stop production and verify that no wood is produced
        woodcutter.stopProduction();

        assertFalse(woodcutter.isProductionEnabled());

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }
    }

    @Test
    public void testProductionInWoodcutterCanBeResumed() throws Exception {

        // Create gamemap
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Plant and grow trees
        Point point12 = new Point(10, 8);
        Tree tree0 = map.placeTree(point12, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Point point13 = new Point(8, 8);
        Tree tree1 = map.placeTree(point13, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree0, map);

        // Place woodcutter
        Point point1 = new Point(8, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter with the headquarters
        Point point2 = new Point(6, 4);
        Point point3 = new Point(8, 4);
        Point point4 = new Point(9, 5);
        Road road0 = map.placeRoad(player0, point2, point3, point4);

        // Finish the woodcutter
        constructHouse(woodcutter);

        // Assign a worker to the woodcutter
        WoodcutterWorker worker = new WoodcutterWorker(player0, map);

        Utils.occupyBuilding(worker, woodcutter);

        assertTrue(worker.isInsideBuilding());

        // Let the worker rest
        Utils.fastForward(100, map);

        // Wait for the worker to produce wood
        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertEquals(worker.getCargo().getMaterial(), WOOD);

        // Wait for the worker to return to the woodcutter hut
        assertEquals(worker.getTarget(), woodcutter.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter.getPosition());

        // Wait for the worker to deliver the cargo
        map.stepTime();

        assertEquals(worker.getTarget(), woodcutter.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter.getFlag().getPosition());

        // Stop production
        woodcutter.stopProduction();

        for (int i = 0; i < 300; i++) {
            assertNull(worker.getCargo());

            map.stepTime();
        }

        // Resume production and verify that the woodcutter produces wood again
        woodcutter.resumeProduction();

        assertTrue(woodcutter.isProductionEnabled());

        Utils.fastForwardUntilWorkerProducesCargo(map, worker);

        assertNotNull(worker.getCargo());
    }

    @Test
    public void testAssignedWoodcutterWorkerHasCorrectlySetPlayer() throws Exception {

        // Create players
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        GameMap map = new GameMap(players, 50, 50);

        // Place headquarters
        Point point0 = new Point(15, 15);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(20, 14);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        // Wait for woodcutter worker to get assigned and leave the headquarters
        List<WoodcutterWorker> workers = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0);

        assertNotNull(workers);
        assertEquals(workers.size(), 1);

        // Verify that the player is set correctly in the worker
        WoodcutterWorker worker = workers.getFirst();

        assertEquals(worker.getPlayer(), player0);
    }

    @Test
    public void testWorkerGoesBackToOwnStorageEvenWithoutRoadsAndEnemiesStorageIsCloser() throws Exception {

        // Create player list with two players
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        Player player1 = new Player("Player 1", PlayerColor.GREEN, Nation.ROMANS, PlayerType.HUMAN);
        Player player2 = new Player("Player 2", PlayerColor.RED, Nation.ROMANS, PlayerType.HUMAN);

        List<Player> players = new LinkedList<>();

        players.add(player0);
        players.add(player1);
        players.add(player2);

        // Create game map choosing two players
        GameMap map = new GameMap(players, 100, 100);

        // Place player 2's headquarters
        Point point10 = new Point(70, 70);
        Headquarter headquarter2 = map.placeBuilding(new Headquarter(player2), point10);

        // Place player 0's headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place player 1's headquarters
        Point point1 = new Point(45, 5);
        Headquarter headquarter1 = map.placeBuilding(new Headquarter(player1), point1);

        // Place fortress for player 0
        Point point2 = new Point(17, 9);
        Building fortress0 = map.placeBuilding(new Fortress(player0), point2);

        // Finish construction of the fortress
        constructHouse(fortress0);

        // Occupy the fortress
        Utils.occupyMilitaryBuilding(PRIVATE_RANK, fortress0);

        // Place woodcutter close to the new border
        Point point4 = new Point(28, 18);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point4);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        WoodcutterWorker worker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Verify that the worker goes back to its own storage when the fortress is torn down
        fortress0.tearDown();

        assertEquals(worker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWoodcutterDoesNotWalkStraightThroughHouse() throws Exception {

        // Create players
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        // Create game map
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(10, 10);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place the woodcutter
        Point point1 = new Point(10, 4);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Place and grow the tree directly behind the woodcutter
        Point point2 = new Point(9, 5);
        Tree tree = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Construct the woodcutter
        constructHouse(woodcutter);

        // Manually place woodcutter worker
        WoodcutterWorker wcWorker = new WoodcutterWorker(player0, map);
        Utils.occupyBuilding(wcWorker, woodcutter);

        // Wait for the woodcutter to rest
        Utils.fastForward(100, map);

        assertFalse(wcWorker.isInsideBuilding());
        assertTrue(wcWorker.isTraveling());

        // Verify that the woodcutter chooses a path that goes via the flag and doesn't go through the house
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));
        assertTrue(wcWorker.getPlannedPath().lastIndexOf(woodcutter.getPosition()) < 1);

        // Let the woodcutter reach the tree and start cutting
        assertEquals(wcWorker.getTarget(), point2);

        Utils.fastForwardUntilWorkersReachTarget(map, wcWorker);

        assertTrue(wcWorker.isArrived());
        assertTrue(wcWorker.isAt(point2));
        assertTrue(wcWorker.isCuttingTree());

        // Wait for the woodcutter to cut down the tree
        Utils.waitForTreeToDisappearFromMap(tree, map);

        map.stepTime();

        // Verify that the woodcutter chooses a path back that goes via the flag
        assertEquals(wcWorker.getTarget(), woodcutter.getPosition());
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getFlag().getPosition()));
        assertTrue(wcWorker.getPlannedPath().contains(woodcutter.getPosition()));
        assertTrue(wcWorker.getPlannedPath().indexOf(woodcutter.getFlag().getPosition()) <
                   wcWorker.getPlannedPath().indexOf(woodcutter.getPosition()));
    }

    @Test
    public void testWoodcutterWorkerReturnsEarlyIfNextPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        // Place woodcutter
        Point point2 = new Point(14, 4);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2.upLeft());

        // Connect headquarters and first flag
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        // Wait for the woodcutter worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) worker;
            }
        }

        assertNotNull(woodcutterWorker);
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the woodcutter worker has started walking
        assertFalse(woodcutterWorker.isExactlyAtPoint());

        // Remove the next road
        map.removeRoad(road1);

        // Verify that the woodcutter worker continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, flag0.getPosition());

        assertEquals(woodcutterWorker.getPosition(), flag0.getPosition());

        // Verify that the woodcutter worker returns to the headquarters when it reaches the flag
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());
    }

    @Test
    public void testWoodcutterWorkerContinuesIfCurrentPartOfTheRoadIsRemoved() throws Exception {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        // Place woodcutter
        Point point2 = new Point(14, 4);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2.upLeft());

        // Connect headquarters and first flag
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        // Wait for the woodcutter worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) worker;
            }
        }

        assertNotNull(woodcutterWorker);
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        // See that the woodcutter worker has started walking
        assertFalse(woodcutterWorker.isExactlyAtPoint());

        // Remove the current road
        map.removeRoad(road0);

        // Verify that the woodcutter worker continues walking to the flag
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, flag0.getPosition());

        assertEquals(woodcutterWorker.getPosition(), flag0.getPosition());

        // Verify that the woodcutter worker continues to the final flag
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getFlag().getPosition());

        // Verify that the woodcutter worker goes out to woodcutter instead of going directly back
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWoodcutterWorkerReturnsToStorageIfWoodcutterIsDestroyed() throws Exception {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place first flag
        Point point1 = new Point(10, 4);
        Flag flag0 = map.placeFlag(player0, point1);

        // Place woodcutter
        Point point2 = new Point(14, 4);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2.upLeft());

        // Connect headquarters and first flag
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), flag0);

        // Connect the first flag with the second flag
        Road road1 = map.placeAutoSelectedRoad(player0, flag0, woodcutter0.getFlag());

        // Wait for the woodcutter worker to be on the second road on its way to the flag
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0);

        WoodcutterWorker woodcutterWorker = null;

        for (Worker worker : map.getWorkers()) {
            if (worker instanceof WoodcutterWorker) {
                woodcutterWorker = (WoodcutterWorker) worker;
            }
        }

        assertNotNull(woodcutterWorker);
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        // Wait for the woodcutter worker to reach the first flag
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, flag0.getPosition());

        map.stepTime();

        // See that the woodcutter worker has started walking
        assertFalse(woodcutterWorker.isExactlyAtPoint());

        // Tear down the woodcutter
        woodcutter0.tearDown();

        // Verify that the woodcutter worker continues walking to the next flag
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getFlag().getPosition());

        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getFlag().getPosition());

        // Verify that the woodcutter worker goes back to storage
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());
    }

    @Test
    public void testWoodcutterWorkerGoesOffroadBackToClosestStorageWhenWoodcutterIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(13, 13);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Place a second storage closer to the woodcutter
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        constructHouse(storehouse0);

        // Destroy the woodcutter
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), storehouse0.getPosition());

        int amount = storehouse0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, storehouse0.getPosition());

        // Verify that the woodcutter worker is stored correctly in the headquarters
        assertEquals(storehouse0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerReturnsOffroadAndAvoidsBurningStorageWhenWoodcutterIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(13, 13);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Place a second storage closer to the woodcutter
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Destroy the woodcutter
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());

        // Verify that the woodcutter worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerReturnsOffroadAndAvoidsDestroyedStorageWhenWoodcutterIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(13, 13);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Place a second storage closer to the woodcutter
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Finish construction of the storage
        constructHouse(storehouse0);

        // Destroy the storage
        storehouse0.tearDown();

        // Wait for the storage to burn down
        Utils.waitForBuildingToBurnDown(storehouse0);

        // Destroy the woodcutter
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());

        // Verify that the woodcutter worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWoodcutterWorkerReturnsOffroadAndAvoidsUnfinishedStorageWhenWoodcutterIsDestroyed() throws Exception {

        // Creating new game map with size 40x40
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(13, 13);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Occupy the woodcutter
        Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Place a second storage closer to the woodcutter
        Point point2 = new Point(7, 13);
        Storehouse storehouse0 = map.placeBuilding(new Storehouse(player0), point2);

        // Destroy the woodcutter
        Worker woodcutterWorker = woodcutter0.getWorker();

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getPosition());

        woodcutter0.tearDown();

        // Verify that the worker leaves the building and goes back to the headquarters
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());

        int amount = headquarter0.getAmount(WOODCUTTER_WORKER);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getPosition());

        // Verify that the woodcutter worker is stored correctly in the headquarters
        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), amount + 1);
    }

    @Test
    public void testWorkerDoesNotEnterBurningBuilding() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point25 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point25);

        // Place woodcutter
        Point point26 = new Point(13, 13);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point26);

        // Place road to connect the headquarters and the woodcutter
        Road road0 = map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Wait for a worker to start walking to the building
        Worker worker = Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 1, player0).getFirst();

        // Wait for the worker to get to the building's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        // Tear down the building
        woodcutter0.tearDown();

        // Verify that the worker goes to the building and then returns to the headquarters instead of entering
        assertEquals(worker.getTarget(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getPosition());

        assertEquals(worker.getTarget(), headquarter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());
    }

    @Test
    public void testTwoWoodcuttersTryToCutDownSameTree() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 20, 20);

        // Place headquarters
        Point point0 = new Point(9, 9);
        map.placeBuilding(new Headquarter(player0), point0);

        // Place and grow the tree
        Point point1 = new Point(12, 12);
        Tree tree = map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.fastForwardUntilTreeIsGrown(tree, map);

        // Place the woodcutters
        Point point2 = new Point(7, 5);
        Point point3 = new Point(13, 5);

        assertNotNull(map.isAvailableHousePoint(player0, point2));
        assertNotNull(map.isAvailableHousePoint(player0, point3));

        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);
        Woodcutter woodcutter1 = map.placeBuilding(new Woodcutter(player0), point3);

        // Construct the woodcutters
        constructHouse(woodcutter0);
        constructHouse(woodcutter1);

        // Manually place woodcutters
        WoodcutterWorker wcWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);
        WoodcutterWorker wcWorker1 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter1);

        // Wait for the woodcutters to leave the buildings and try to cut down the same tree
        Utils.waitForWorkersOutsideBuilding(WoodcutterWorker.class, 2, player0);

        assertEquals(wcWorker0.getTarget(), tree.getPosition());
        assertEquals(wcWorker1.getTarget(), tree.getPosition());

        // Let the woodcutters reach the tree and start cutting
        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker0, tree.getPosition());

        assertEquals(wcWorker0.getPosition(), tree.getPosition());
        assertEquals(wcWorker1.getPosition(), tree.getPosition());

        // Wait for one of them to cut down the tree
        assertTrue(wcWorker0.isCuttingTree() || wcWorker1.isCuttingTree());

        // Wait for the woodcutter to finish cutting the tree
        for (int i = 0; i < 1000; i++) {
            if (tree.isFalling()) {
                break;
            }

            assertTrue(wcWorker0.isCuttingTree() || wcWorker1.isCuttingTree());
            assertTrue(map.isTreeAtPoint(tree.getPosition()));

            map.stepTime();
        }

        assertTrue(tree.isFalling());
        assertFalse(wcWorker0.isCuttingTree());
        assertFalse(wcWorker1.isCuttingTree());

        Utils.waitForTreeToDisappearFromMap(tree, map);

        assertFalse(map.isTreeAtPoint(tree.getPosition()));

        // Verify that one of the woodcutters got the wood and both are going back
        map.stepTime();

        assertTrue(wcWorker0.getCargo() == null || wcWorker1.getCargo() == null);
        assertTrue(wcWorker0.getCargo() != null || wcWorker1.getCargo() != null);
        assertTrue((wcWorker0.getCargo() != null && wcWorker0.getCargo().getMaterial().equals(WOOD)) ||
                   (wcWorker1.getCargo() != null && wcWorker1.getCargo().getMaterial().equals(WOOD)));

        // Verify that both woodcutters go back home
        assertEquals(wcWorker0.getTarget(), woodcutter0.getPosition());
        assertEquals(wcWorker1.getTarget(), woodcutter1.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker0, woodcutter0.getPosition());

        if (!wcWorker1.getPosition().equals(woodcutter1.getPosition())) {
            Utils.fastForwardUntilWorkerReachesPoint(map, wcWorker1, woodcutter1.getPosition());
        }

        assertTrue(wcWorker0.isInsideBuilding());
        assertTrue(wcWorker1.isInsideBuilding());
    }

    @Test
    public void testWoodcutterHutWithoutResourcesHasZeroProductivity() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter hut
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Finish construction of the woodcutter hut
        constructHouse(woodcutter0);

        // Populate the woodcutter hut
        Worker woodcutterWorker = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        assertTrue(woodcutterWorker.isInsideBuilding());
        assertEquals(woodcutterWorker.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker);

        // Verify that the productivity is 0% when the woodcutter hut doesn't produce anything
        for (int i = 0; i < 500; i++) {
            assertTrue(woodcutter0.getFlag().getStackedCargo().isEmpty());
            assertNull(woodcutterWorker.getCargo());
            assertEquals(woodcutter0.getProductivity(), 0);
            map.stepTime();
        }
    }

    @Test
    public void testWoodcutterHutWithAbundantResourcesHasFullProductivity() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter hut
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Connect the woodcutter hut with the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        // Wait for the woodcutter hut to get constructed and populated
        Utils.waitForBuildingToBeConstructed(woodcutter0);

        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        // Disable production
        woodcutter0.stopProduction();

        // Place a lot of trees on the map
        Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 6), map);

        // Wait for the trees to grow
        Utils.fastForward(1000, map);

        // Enable production again
        woodcutter0.resumeProduction();

        // Wait for the woodcutter hut to reach full productivity
        for (int i = 0; i < 20000; i++) {
            if (woodcutter0.getProductivity() == 100) {
                break;
            }

            map.stepTime();
        }

        // Verify that the productivity is 100% and stays there
        assertEquals(woodcutter0.getProductivity(), 100);

        for (int i = 0; i < 2000; i++) {
            map.stepTime();

            assertEquals(woodcutter0.getProductivity(), 100);
        }
    }

    @Test
    public void testWoodcutterHutLosesProductivityWhenResourcesRunOut() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter hut
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Finish construction of the woodcutter hut
        constructHouse(woodcutter0);

        // Populate the woodcutter hut
        Worker woodcutterWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        assertTrue(woodcutterWorker0.isInsideBuilding());
        assertEquals(woodcutterWorker0.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker0);

        // Connect the woodcutter hut with the headquarters
        map.placeAutoSelectedRoad(player0, headquarter0.getFlag(), woodcutter0.getFlag());

        // Place a lot of trees on the map
        Utils.plantTreesOnPoints(map.getPointsWithinRadius(woodcutter0.getPosition(), 4), map);

        // Wait for the trees to grow up
        Utils.fastForward(300, map);

        // Make the woodcutter take down trees until the trees are gone
        for (int i = 0; i < 5000; i++) {

            map.stepTime();

            if (map.getTrees().isEmpty()) {
                break;
            }
        }

        assertEquals(woodcutter0.getProductivity(), 100);

        // Verify that the productivity goes down when resources run out
        for (int i = 0; i < 2000; i++) {
            map.stepTime();
        }

        assertEquals(woodcutter0.getProductivity(), 0);
    }

    @Test
    public void testUnoccupiedWoodcutterHutHasNoProductivity() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter hut
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Finish construction of the woodcutter hut
        constructHouse(woodcutter0);

        // Verify that the unoccupied woodcutter hut is unproductive
        for (int i = 0; i < 1000; i++) {
            assertEquals(woodcutter0.getProductivity(), 0);

            map.stepTime();
        }
    }

    @Test
    public void testWoodcutterCanProduce() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(10, 10);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Finish construction of the woodcutter
        constructHouse(woodcutter0);

        // Populate the woodcutter
        Worker woodcutterWorker0 = Utils.occupyBuilding(new WoodcutterWorker(player0, map), woodcutter0);

        // Verify that the hunter hut can produce
        assertTrue(woodcutter0.canProduce());
    }

    @Test
    public void testWoodcutterReportsCorrectOutput() throws Exception {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the woodcutter
        constructHouse(woodcutter0);

        // Verify that the reported output is correct
        assertEquals(woodcutter0.getProducedMaterial().length, 1);
        assertEquals(woodcutter0.getProducedMaterial()[0], WOOD);
    }

    @Test
    public void testWoodcutterReportsCorrectMaterialsNeededForConstruction() throws Exception {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Verify that the reported needed construction material is correct
        assertEquals(woodcutter0.getTypesOfMaterialNeeded().size(), 1);
        assertTrue(woodcutter0.getTypesOfMaterialNeeded().contains(PLANK));
        assertEquals(woodcutter0.getCanHoldAmount(PLANK), 2);

        for (Material material : Material.values()) {
            if (material == PLANK) {
                continue;
            }

            assertEquals(woodcutter0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testWoodcutterReportsCorrectMaterialsNeededForProduction() throws Exception {

        // Starting new game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(6, 12);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Construct the woodcutter
        constructHouse(woodcutter0);

        // Verify that the reported needed construction material is correct
        assertEquals(woodcutter0.getTypesOfMaterialNeeded().size(), 0);

        for (Material material : Material.values()) {
            assertEquals(woodcutter0.getCanHoldAmount(material), 0);
        }
    }

    @Test
    public void testWoodcutterWaitsWhenFlagIsFull() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 30, 30);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(16, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Place trees
        Point point2 = new Point(18, 6);
        Point point3 = new Point(19, 7);
        Point point4 = new Point(20, 6);
        Tree tree0 = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        Tree tree1 = map.placeTree(point3, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        Tree tree2 = map.placeTree(point4, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Wait for the woodcutter to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(woodcutter);
        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        // Fill the flag with flour cargos
        Utils.placeCargos(map, FLOUR, 8, woodcutter.getFlag(), headquarter);

        // Remove the road
        map.removeRoad(road0);

        // Verify that the woodcutter waits for the flag to get empty and produces nothing
        for (int i = 0; i < 600; i++) {
            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        // Reconnect the woodcutter with the headquarters
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Wait for the courier to pick up one of the cargos
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 700; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertNull(courier.getCargo());
            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 7);

        // Verify that the worker produces a cargo of flour and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(map, woodcutter.getWorker(), WOOD);
    }

    @Test
    public void testWoodcutterDeliversThenWaitsWhenFlagIsFullAgain() throws Exception {

        // Create single player game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);

        GameMap map = new GameMap(players, 30, 30);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(16, 6);
        Woodcutter woodcutter = map.placeBuilding(new Woodcutter(player0), point1);

        // Place trees
        Point point2 = new Point(18, 6);
        Point point3 = new Point(19, 7);
        Point point4 = new Point(20, 6);
        Tree tree0 = map.placeTree(point2, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        Tree tree1 = map.placeTree(point3, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
        Tree tree2 = map.placeTree(point4, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Wait for the woodcutter to get constructed and assigned a worker
        Utils.waitForBuildingToBeConstructed(woodcutter);
        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter);

        // Fill the flag with cargos
        Utils.placeCargos(map, FLOUR, 8, woodcutter.getFlag(), headquarter);

        // Remove the road
        map.removeRoad(road0);

        // The woodcutter waits for the flag to get empty and produces nothing
        for (int i = 0; i < 500; i++) {
            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        // Reconnect the woodcutter with the headquarters
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter.getFlag(), headquarter.getFlag());

        // Wait for the courier to pick up one of the cargos
        Courier courier = Utils.waitForRoadToGetAssignedCourier(map, road1);

        for (int i = 0; i < 600; i++) {
            if (courier.getCargo() != null && courier.getCargo().getMaterial() == FLOUR) {
                break;
            }

            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }

        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 7);

        // Remove the road
        map.removeRoad(road1);

        // The worker produces a cargo and puts it on the flag
        Utils.fastForwardUntilWorkerCarriesCargo(map, woodcutter.getWorker(), WOOD);

        // Wait for the worker to put the cargo on the flag
        Utils.waitForFlagToGetStackedCargo(map, woodcutter.getFlag(), 8);

        assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

        // Verify that the woodcutter doesn't produce anything because the flag is full
        for (int i = 0; i < 600; i++) {
            assertEquals(woodcutter.getFlag().getStackedCargo().size(), 8);

            map.stepTime();
        }
    }

    @Test
    public void testWhenWoodDeliveryAreBlockedWoodcutterFillsUpFlagAndThenStops() throws Exception {

        // Start new game with one player only
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place Woodcutter
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Place road to connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        // Place trees
        for (Point point : Utils.getAreaInsideHexagon(6, woodcutter0.getPosition())) {
            try {
                map.placeTree(point, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);
            } catch (Exception e) {}
        }

        // Wait for the woodcutter to get constructed and occupied
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        Utils.waitForBuildingToBeConstructed(woodcutter0);

        Worker woodcutterWorker0 = Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        assertTrue(woodcutterWorker0.isInsideBuilding());
        assertEquals(woodcutterWorker0.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker0);

        // Block storage of wood
        headquarter0.blockDeliveryOfMaterial(WOOD);

        // Verify that the woodcutter puts eight wood pieces on the flag and then stops
        Utils.waitForFlagToGetStackedCargo(map, woodcutter0.getFlag(), 8);

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker0, woodcutter0.getPosition());

        for (int i = 0; i < 300; i++) {
            map.stepTime();

            assertEquals(woodcutter0.getFlag().getStackedCargo().size(), 8);

            if (road0.getCourier().getCargo() != null) {
                assertNotEquals(road0.getCourier().getCargo().getMaterial(), WOOD);
            }
        }
    }

    @Test
    public void testWorkerGoesToOtherStorageWhereStorageIsBlockedAndWoodcutterIsTornDown() throws Exception {

        // Start new game with one player only
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place woodcutter
        Point point2 = new Point(18, 6);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        // Place road to connect the storehouse with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the woodcutter
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the woodcutter and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, woodcutter0);

        // Wait for the woodcutter and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, woodcutter0);

        Worker woodcutterWorker0 = woodcutter0.getWorker();

        assertTrue(woodcutterWorker0.isInsideBuilding());
        assertEquals(woodcutterWorker0.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker0);

        // Verify that the worker goes to the storage when the woodcutter is torn down
        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);

        woodcutter0.tearDown();

        map.stepTime();

        assertFalse(woodcutterWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker0, woodcutter0.getFlag().getPosition());

        assertEquals(woodcutterWorker0.getTarget(), storehouse.getPosition());

        Utils.verifyWorkerWalksToTargetOnRoads(map, woodcutterWorker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(woodcutterWorker0));
    }

    @Test
    public void testWorkerGoesToOtherStorageOffRoadWhereStorageIsBlockedAndWoodcutterIsTornDown() throws Exception {

        // Start new game with one player only
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place storehouse
        Point point1 = new Point(5, 5);
        Storehouse storehouse = map.placeBuilding(new Storehouse(player0), point1);

        // Place woodcutter
        Point point2 = new Point(18, 6);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        // Place road to connect the storehouse with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, storehouse.getFlag(), headquarter0.getFlag());

        // Place road to connect the headquarters with the woodcutter
        Road road1 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        // Add a lot of planks and stones to the headquarters
        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the woodcutter and the storehouse to get constructed
        Utils.waitForBuildingsToBeConstructed(storehouse, woodcutter0);

        // Wait for the woodcutter and the storage to get occupied
        Utils.waitForNonMilitaryBuildingsToGetPopulated(storehouse, woodcutter0);

        Worker woodcutterWorker0 = woodcutter0.getWorker();

        assertTrue(woodcutterWorker0.isInsideBuilding());
        assertEquals(woodcutterWorker0.getHome(), woodcutter0);
        assertEquals(woodcutter0.getWorker(), woodcutterWorker0);

        // Verify that the worker goes to the storage off-road when the woodcutter is torn down
        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);

        woodcutter0.tearDown();

        map.removeRoad(road0);

        map.stepTime();

        assertFalse(woodcutterWorker0.isInsideBuilding());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker0, woodcutter0.getFlag().getPosition());

        assertEquals(woodcutterWorker0.getTarget(), storehouse.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker0, storehouse.getPosition());

        assertFalse(map.getWorkers().contains(woodcutterWorker0));
    }

    @Test
    public void testWorkerGoesOutAndBackInWhenSentOutWithoutBlocking() throws Exception {

        // Start new game with one player only
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that worker goes out and in continuously when sent out without being blocked
        Utils.adjustInventoryTo(headquarter0, WOODCUTTER_WORKER, 1);

        assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), 1);

        headquarter0.pushOutAll(WOODCUTTER_WORKER);

        for (int i = 0; i < 10; i++) {
            Worker worker = Utils.waitForWorkerOutsideBuilding(WoodcutterWorker.class, player0);

            assertEquals(headquarter0.getAmount(WOODCUTTER_WORKER), 0);
            assertEquals(worker.getPosition(), headquarter0.getPosition());
            assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

            assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
            assertEquals(worker.getTarget(), headquarter0.getPosition());

            Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getPosition());

            assertFalse(map.getWorkers().contains(worker));
        }
    }

    @Test
    public void testPushedOutWorkerWithNowhereToGoWalksAwayAndDies() throws Exception {

        // Start new game with one player only
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Verify that worker goes out and in continuously when sent out without being blocked
        Utils.adjustInventoryTo(headquarter0, WOODCUTTER_WORKER, 1);

        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);
        headquarter0.pushOutAll(WOODCUTTER_WORKER);

        Worker worker = Utils.waitForWorkerOutsideBuilding(WoodcutterWorker.class, player0);

        assertEquals(worker.getPosition(), headquarter0.getPosition());
        assertEquals(worker.getTarget(), headquarter0.getFlag().getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, headquarter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), headquarter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerWithNowhereToGoWalksAwayAndDiesWhenHouseIsTornDown() throws Exception {

        // Start new game with one player only
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Place road to connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the woodcutter to get constructed and occupied
        Utils.waitForBuildingToBeConstructed(woodcutter0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        /* Verify that worker goes out and then walks away and dies when the building is torn down because delivery is
           blocked in the headquarter */
       
        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);

        Worker worker = woodcutter0.getWorker();

        woodcutter0.tearDown();

        assertEquals(worker.getPosition(), woodcutter0.getPosition());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, woodcutter0.getFlag().getPosition());

        assertEquals(worker.getPosition(), woodcutter0.getFlag().getPosition());
        assertNotNull(worker.getTarget());
        assertNotEquals(worker.getTarget(), woodcutter0.getPosition());
        assertNotEquals(worker.getTarget(), headquarter0.getPosition());
        assertFalse(worker.isDead());

        Utils.fastForwardUntilWorkerReachesPoint(map, worker, worker.getTarget());

        assertTrue(worker.isDead());

        for (int i = 0; i < 100; i++) {
            assertTrue(worker.isDead());
            assertTrue(map.getWorkers().contains(worker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(worker));
    }

    @Test
    public void testWorkerGoesAwayAndDiesWhenItReachesTornDownHouseAndStorageIsBlocked() throws Exception {

        // Start new game with one player only
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(12, 6);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place woodcutter
        Point point1 = new Point(7, 9);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point1);

        // Place road to connect the woodcutter with the headquarters
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        Utils.adjustInventoryTo(headquarter0, PLANK, 30);
        Utils.adjustInventoryTo(headquarter0, STONE, 30);

        // Wait for the woodcutter to get constructed
        Utils.waitForBuildingToBeConstructed(woodcutter0);

        // Wait for a woodcutter worker to start walking to the woodcutter
        WoodcutterWorker woodcutterWorker = Utils.waitForWorkerOutsideBuilding(WoodcutterWorker.class, player0);

        // Wait for the woodcutter worker to go past the headquarter's flag
        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, headquarter0.getFlag().getPosition());

        map.stepTime();

        // Verify that the woodcutter worker goes away and dies when the house has been torn down and storage is not possible
        assertEquals(woodcutterWorker.getTarget(), woodcutter0.getPosition());

        headquarter0.blockDeliveryOfMaterial(WOODCUTTER_WORKER);

        woodcutter0.tearDown();

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutter0.getFlag().getPosition());

        assertEquals(woodcutterWorker.getPosition(), woodcutter0.getFlag().getPosition());
        assertNotEquals(woodcutterWorker.getTarget(), headquarter0.getPosition());
        assertFalse(woodcutterWorker.isInsideBuilding());
        assertNull(woodcutter0.getWorker());
        assertNotNull(woodcutterWorker.getTarget());

        Utils.fastForwardUntilWorkerReachesPoint(map, woodcutterWorker, woodcutterWorker.getTarget());

        Point point = woodcutterWorker.getPosition();
        for (int i = 0; i < 100; i++) {
            assertTrue(woodcutterWorker.isDead());
            assertEquals(woodcutterWorker.getPosition(), point);
            assertTrue(map.getWorkers().contains(woodcutterWorker));

            map.stepTime();
        }

        assertFalse(map.getWorkers().contains(woodcutterWorker));
    }

    @Test
    public void testWoodcutterIgnoresUnreachableTree() throws InvalidUserActionException {

        // Create game
        Player player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        List<Player> players = new ArrayList<>();
        players.add(player0);
        GameMap map = new GameMap(players, 40, 40);

        // Place headquarters
        Point point0 = new Point(5, 5);
        Headquarter headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place a tree surrounded by a lake
        var point1 = new Point(12, 8);
        var tree0 = map.placeTree(point1, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        Utils.surroundPointWithWater(point1.left().upLeft(), map);
        Utils.surroundPointWithWater(point1.up(), map);
        Utils.surroundPointWithWater(point1.right().upRight(), map);
        Utils.surroundPointWithWater(point1.right().downRight(), map);
        Utils.surroundPointWithWater(point1.down(), map);
        Utils.surroundPointWithWater(point1.left().downLeft(), map);

        // Place woodcutter
        Point point2 = new Point(12, 4);
        Woodcutter woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);

        // Place road to connect the woodcutter with the headquarters and wait for it to get constructed and occupied
        Road road0 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());

        Utils.waitForBuildingToBeConstructed(woodcutter0);
        Utils.waitForNonMilitaryBuildingToGetPopulated(woodcutter0);

        // Verify that the woodcutter worker stays in the house and doesn't go out to cut the tree
        assertNull(map.findWayOffroad(woodcutter0.getFlag().getPosition(), tree0.getPosition(), null));

        for (int i = 0; i < 2_000; i++) {
            assertTrue(woodcutter0.getWorker().isInsideBuilding());

            map.stepTime();
        }
    }

    @Test
    public void testWoodcutterDoesNotGoOutToCutDownTreeThatIsAlreadyBeingCutDown() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place two woodcutters and connect them to the headquarters
        var point2 = new Point(10, 10);
        var point3 = new Point(14, 10);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);
        var woodcutter1 = map.placeBuilding(new Woodcutter(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, woodcutter1.getFlag(), headquarter0.getFlag());

        // Place a single tree for both woodcutters
        var point4 = new Point(12, 10);

        map.placeTree(point4, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Wait for the farms to get constructed and occupied
        Utils.waitForBuildingsToBeConstructed(woodcutter0, woodcutter1);
        Utils.waitForNonMilitaryBuildingsToGetPopulated(woodcutter0, woodcutter1);

        // Wait for the first woodcutter worker to start cutting down the tree
        var woodcutterWorker0 = (WoodcutterWorker) woodcutter0.getWorker();

        for (int i = 0; i < 2_000; i++) {
            if (woodcutterWorker0.isCuttingTree()) {
                break;
            }

            woodcutterWorker0.stepTime();
        }

        assertTrue(woodcutterWorker0.isCuttingTree());

        // Verify that the second woodcutterWorker0 doesn't go out to cut down the tree. Cheat by only stepping its time.
        var woodcutterWorker1 = (WoodcutterWorker) woodcutter1.getWorker();

        for (int i = 0; i < 2_000; i++) {
            assertFalse(woodcutterWorker1.isCuttingTree());
            assertTrue(woodcutterWorker0.isCuttingTree());

            woodcutterWorker1.stepTime();
        }
    }

    @Test
    public void testWoodcutterDoesNotCutDownTreeThatIsAlreadyBeingCutDown() throws InvalidUserActionException {

        // Start new game with one player only
        var player0 = new Player("Player 0", PlayerColor.BLUE, Nation.ROMANS, PlayerType.HUMAN);
        var map = new GameMap(List.of(player0), 40, 40);

        // Place headquarters
        var point0 = new Point(12, 6);
        var headquarter0 = map.placeBuilding(new Headquarter(player0), point0);

        // Place two woodcutters and connect them to the headquarters
        var point2 = new Point(10, 10);
        var point3 = new Point(14, 10);
        var woodcutter0 = map.placeBuilding(new Woodcutter(player0), point2);
        var woodcutter1 = map.placeBuilding(new Woodcutter(player0), point3);
        var road1 = map.placeAutoSelectedRoad(player0, woodcutter0.getFlag(), headquarter0.getFlag());
        var road2 = map.placeAutoSelectedRoad(player0, woodcutter1.getFlag(), headquarter0.getFlag());

        // Place one tree for them to want to cut down
        var point4 = new Point(12, 10);

        map.placeTree(point4, Tree.TreeType.PINE, Tree.TreeSize.FULL_GROWN);

        // Wait for the woodcutters to get constructed and occupied
        Utils.waitForBuildingsToBeConstructed(woodcutter0, woodcutter1);
        Utils.waitForNonMilitaryBuildingsToGetPopulated(woodcutter0, woodcutter1);

        var woodcutterWorker0 = (WoodcutterWorker) woodcutter0.getWorker();

        // Wait for the second woodcutter worker to go out to cut down the tree. Cheat by only stepping its time
        var woodcutterWorker1 = (WoodcutterWorker) woodcutter1.getWorker();

        for (int i = 0; i < 2_000; i++) {
            if (!woodcutterWorker1.isInsideBuilding()) {
                break;
            }

            woodcutterWorker1.stepTime();
        }

        assertFalse(woodcutterWorker1.isInsideBuilding());
        assertEquals(woodcutterWorker1.getTarget(), point4);

        // Wait for the first woodcutter worker to start cutting down the tree. Cheat by only stepping its time
        for (int i = 0; i < 2_000; i++) {
            if (woodcutterWorker0.isCuttingTree()) {
                break;
            }

            woodcutterWorker0.stepTime();
        }

        assertTrue(woodcutterWorker0.isCuttingTree());
        assertEquals(woodcutterWorker0.getPosition(), point4);

        // Verify that the second woodcutter worker doesn't start cutting down the tree and instead goes back home.
        // Cheat by only stepping its time.
        for (int i = 0; i < 2_000; i++) {
            if (woodcutterWorker1.getPosition().equals(point4)) {
                break;
            }

            assertEquals(woodcutterWorker1.getTarget(), point4);

            woodcutterWorker1.stepTime();
        }

        assertTrue(woodcutterWorker0.isCuttingTree());
        assertEquals(woodcutterWorker0.getPosition(), point4);
        assertEquals(woodcutterWorker1.getPosition(), point4);
        assertFalse(woodcutterWorker1.isCuttingTree());
        assertEquals(woodcutterWorker1.getTarget(), woodcutter1.getPosition());

        for (int i = 0; i < 2_000; i++) {
            if (woodcutterWorker1.isInsideBuilding()) {
                break;
            }

            assertFalse(woodcutterWorker1.isCuttingTree());
            assertEquals(woodcutterWorker1.getTarget(), woodcutter1.getPosition());

            woodcutterWorker1.stepTime();
        }

        assertTrue(woodcutterWorker1.isInsideBuilding());
        assertFalse(woodcutterWorker1.isCuttingTree());
        assertEquals(woodcutterWorker1.getPosition(), woodcutter1.getPosition());
    }
}
